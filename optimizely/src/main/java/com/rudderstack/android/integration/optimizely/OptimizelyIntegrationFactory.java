package com.rudderstack.android.integration.optimizely;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.rudderstack.android.sdk.core.MessageType;
import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderIntegration;
import com.rudderstack.android.sdk.core.RudderLogger;
import com.rudderstack.android.sdk.core.RudderMessage;
import com.rudderstack.android.sdk.core.ecomm.ECommerceEvents;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OptimizelyIntegrationFactory extends RudderIntegration<Void> {
    private static final String FIREBASE_KEY = "Optimizely";

    public static Factory FACTORY = new Factory() {
        @Override
        public RudderIntegration<?> create(@Nullable Object settings, @NonNull RudderClient client, @NonNull RudderConfig rudderConfig) {
            RudderLogger.logDebug("Creating RudderIntegrationFactory");
            return new OptimizelyIntegrationFactory(settings, client, rudderConfig);
        }

        @Override
        public String key() {
            return FIREBASE_KEY;
        }
    };

    private OptimizelyIntegrationFactory(@Nullable Object config, @NonNull RudderClient client, @NonNull RudderConfig rudderConfig) {
    }

    private void processRudderEvent(@NonNull RudderMessage element) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void dump(@Nullable RudderMessage element) {
        if (element != null) {
            processRudderEvent(element);
        }
    }
}
