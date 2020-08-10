package com.rudderstack.android.integration.optimizely;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.optimizely.ab.android.sdk.OptimizelyClient;
import com.optimizely.ab.android.sdk.OptimizelyManager;
import com.optimizely.ab.config.Experiment;
import com.optimizely.ab.config.Variation;
import com.optimizely.ab.event.LogEvent;
import com.optimizely.ab.notification.ActivateNotificationListener;
import com.rudderstack.android.sdk.core.MessageType;
import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderIntegration;
import com.rudderstack.android.sdk.core.RudderLogger;
import com.rudderstack.android.sdk.core.RudderMessage;
import com.rudderstack.android.sdk.core.RudderProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public class OptimizelyIntegrationFactory extends RudderIntegration<OptimizelyClient> {
    private static final String OPTIMIZELYX_KEY = "Optimizely Fullstack";

    private boolean trackKnownUsers;
    private static boolean nonInteraction;
    private boolean listen;

    private int notificationId;
    private OptimizelyClient optimizelyClient;
    private Map<String, Object> attributes = new HashMap<>();
    List<RudderMessage> trackEvents = new ArrayList<>();

    // TODO : Use RudderOptions for Experiment Viewed event
    public static class FACTORY implements Factory {
        private OptimizelyManager manager;

        public FACTORY(OptimizelyManager manager) {
            this.manager = manager;
        }

        @Override
        public RudderIntegration<?> create(Object settings, RudderClient client, RudderConfig config) {
            return new OptimizelyIntegrationFactory(settings, client, manager);
        }

        @Override
        public String key() {
            return OPTIMIZELYX_KEY;
        }
    }

    private OptimizelyIntegrationFactory(@Nullable Object config, @NonNull RudderClient client, @NonNull OptimizelyManager manager) {
        Map<String, Object> configMap = (Map<String, Object>) config;

        if (configMap != null && client.getApplication() != null) {
            optimizelyClient = manager.getOptimizely();

            trackKnownUsers = (boolean) configMap.get("trackKnownUsers");
            nonInteraction = (boolean) configMap.get("nonInteraction");
            listen = (boolean) configMap.get("listen");

            if (optimizelyClient.isValid()) {
                if (listen) {
                    notificationId = createListener(client);
                }
            } else {
                // poll the optimizely client till it is valid
                pollOptimizelyClient(manager, client);
            }

        }
    }

    @Override
    public void dump(@Nullable RudderMessage element) {
        try {
            if (element != null) {
                this.processEvents(element);
            }
        } catch (Exception e) {
            RudderLogger.logError(e);
        }
    }

    @Override
    public OptimizelyClient getUnderlyingInstance() {
        return optimizelyClient;
    }

    @Override
    public void reset() {
        // remove notification listener
        optimizelyClient.getNotificationCenter().removeNotificationListener(notificationId);
        RudderLogger.logVerbose("Removed optimizely notification listener");
    }

    private void processEvents(@NonNull RudderMessage message) {
        String eventType = message.getType();
        if (eventType != null) {
            switch (eventType) {
                case MessageType.IDENTIFY:
                    Map<String, Object> traits = message.getTraits();

                    if (traits != null && !traits.isEmpty()) {
                        attributes.putAll(traits);
                    }
                    break;
                case MessageType.TRACK:
                    track(message);
                    break;
                default:
                    RudderLogger.logWarn("Message type is not supported");
            }
        }
    }

    private void track(RudderMessage message) {
        synchronized (this) {
            // if the optimizelyClient is invalid, enqueue track event to trackEvents
            if (!optimizelyClient.isValid()) {
                RudderLogger.logVerbose(String.format("Optimizely not initialized. Enqueueing action: %s", message));
                if (trackEvents.size() >= 100) {
                    RudderLogger.logVerbose(String.format(
                            "Event queue has exceeded limit. Dropping event at index zero: %s",
                            trackEvents.get(0)));
                    trackEvents.remove(0);
                }
                trackEvents.add(message);
                return;
            }

            // RudderStack will send `track` calls with `anonymousId`s by default, since Optimizely X does not alias known and unknown users
            // https://developers.optimizely.com/x/solutions/sdks/reference/index.html?language=objectivec&platform=mobile#user-ids
            String userId = message.getUserId();

            if (trackKnownUsers && isEmpty(userId)) {
                RudderLogger.logVerbose(
                        "RudderStack will only track users associated with a userId "
                                + "when the trackKnownUsers setting is enabled.");
                return;
            }

            String id = message.getAnonymousId();
            String event = message.getEventName();
            Map<String, Object> properties = message.getProperties();

            if (trackKnownUsers && !isEmpty(userId)) {
                id = userId;
            }


            if (properties != null) {
                optimizelyClient.track(event, id, attributes, properties);
            } else {
                optimizelyClient.track(event, id, attributes);
            }


            RudderLogger.logVerbose(String.format("optiizelyClient.track(%s, %s, %s, %s)", event, id, attributes, properties));
        }
    }

    private int createListener(RudderClient client){
        return optimizelyClient.getNotificationCenter()
                .addActivateNotificationListener(new OptimizelyNotificationListener(client));
    }

    /*
    Checks whether the optimizelyClient has become valid every 60 seconds.If the client has become valid,
    it replays all previous track calls.
     */
    private void pollOptimizelyClient(final OptimizelyManager manager, final RudderClient client) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    synchronized (OptimizelyIntegrationFactory.this) {
                        // get optimizelyClient from the manager
                        OptimizelyIntegrationFactory.this.optimizelyClient = manager.getOptimizely();
                    }
                    if (optimizelyClient.isValid()) {
                        // replay all previous track calls as the optimizelyClient is now valid
                        setClientAndFlushTracks(client);
                        break;
                    }
                    try {
                        RudderLogger.logDebug("Optimizely Client Invalid, retrying in 60 seconds");
                        Thread.sleep(60 * 1000);// sleep for 60 seconds
                    } catch (InterruptedException ex) {
                        RudderLogger.logError(ex);
                    }
                }
            }
        }).start();
    }

    // sets the notification listener and replays all previous track calls
    void setClientAndFlushTracks(RudderClient client) {
        if (listen) {
            notificationId = createListener(client);
        }
        RudderLogger.logVerbose("Flushing track queue");

        for (RudderMessage t : trackEvents) {
            track(t);
        }

        trackEvents = null;
    }

    // class that implements the ActivateNotificationListener interface
    class OptimizelyNotificationListener extends ActivateNotificationListener {
        private final RudderClient client;

        OptimizelyNotificationListener(RudderClient client) {
            this.client = client;
        }

        @Override
        public void onActivate(@NonNull Experiment experiment, @NonNull String userId, @NonNull Map<String, ?> attributes, @NonNull Variation variation, @NonNull LogEvent event) {
            RudderProperty properties =
                    new RudderProperty()
                            .putValue("experimentId", experiment.getId())
                            .putValue("experimentName", experiment.getKey())
                            .putValue("variationId", variation.getId())
                            .putValue("variationName", variation.getKey());

            if (nonInteraction) {
                properties.putValue("nonInteraction", 1);
            }
            client.track("Experiment Viewed", properties);
            // TODO: Pass options
        }
    }
}
