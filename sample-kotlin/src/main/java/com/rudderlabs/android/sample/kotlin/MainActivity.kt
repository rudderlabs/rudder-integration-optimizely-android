package com.rudderlabs.android.sample.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rudderstack.android.sdk.core.RudderMessageBuilder
import com.rudderstack.android.sdk.core.RudderProperty
import com.rudderstack.android.sdk.core.RudderTraits
import com.rudderstack.android.sdk.core.TrackPropertyBuilder
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendEvents()
    }

    private fun sendEvents() {

        MainApplication.rudderClient.screen(localClassName)
        val isEnabled1: Boolean =
            MainApplication.optimizelyClient.isFeatureEnabled("samplefeatureforrudder", "sajal_mohanta1")

        if (isEnabled1) {
            MainApplication.rudderClient.identify(
                "sajal_mohanta1", RudderTraits()
                    .putName("User_id_identify")
                    .putEmail("identify1@test.com")
                    .put("quantity", "5")
                    .put("price", "56.0")
                    .putAge("30")
                    .putBirthday("24th March 1990")
                    .putAddress(
                        RudderTraits.Address(
                            "KOlkata",
                            "India",
                            "700096",
                            "West bengal",
                            "Park Street"
                        )
                    )
                    .putCreatedAt(Date().toString())
                    .putDescription("Sample description")
                    .putFirstName("Sajal")
                    .putLastName("Mohanta")
                    .putBirthday("18th March 2020")
                    .putPhone("9112340345")
                    .putUserName("Samle_putUserName")
                , null
            )
            MainApplication.rudderClient.track("Event2")
        }

        //without `userId` , will be taken from `identify`
        val eventKey = "my_conversion"
        val userId = "user123"

        val attributes: MutableMap<String, String> =
            HashMap()
        attributes["DEVICE"] = "iPhone"
        attributes["AD_SOURCE"] = "my_campaign"

        val eventTags: MutableMap<String, Any> =
            HashMap()
        eventTags["purchasePrice"] = 64.32f
        eventTags["category"] = "shoes"


// Reserved "revenue" tag
        eventTags["revenue"] = 6432
// Reserved "value" tag
        eventTags["value"] = 4
        MainApplication.rudderClient.track(
            RudderMessageBuilder()
                .setEventName("my_conversion1")
                .setProperty(eventTags)
                .setUserProperty(attributes as Map<String, Any>?)
        )
//        MainApplication.rudderClient.track("Experiment Viewed")

        MainApplication.rudderClient.track(
                RudderMessageBuilder()
                        .setEventName("daily_rewards_claim")
                        .setProperty(
                                TrackPropertyBuilder()
                                        .setCategory("test_category")
                                        .build()
                        )
                        .setUserId("test_user_id")
        )

        MainApplication.rudderClient.identify("developer_user_id")

        MainApplication.rudderClient.track(
            RudderMessageBuilder()
                .setEventName("level_up")
                .setProperty(
                    TrackPropertyBuilder()
                        .setCategory("test_category")
                        .build()
                )
                .setUserId("test_user_id")
        )



        val revenueProperty = TrackPropertyBuilder()
                .setCategory("test_category")
                .build()
        revenueProperty.put("total", 4.99)
        revenueProperty.put("currency", "USD")

        MainApplication.rudderClient.track(
                RudderMessageBuilder()
                        .setEventName("revenue")
                        .setProperty(revenueProperty)
                        .setUserId("test_user_id")
        )
        MainApplication.rudderClient.reset()
        // Implementation of event tags



        // with `userId`
        MainApplication.rudderClient.track(
                RudderMessageBuilder()
                        .setEventName(eventKey)
                        .setProperty(eventTags)
//                        .setUserId(userId)
                        .setUserProperty(attributes as Map<String, Any>?)
        )





        val property = RudderProperty()
        property.put("key_1", "val_1")
        property.put("revenue", 20)

        val childProperty = RudderProperty()
        childProperty.put("key_c_1", "val_c_1")
        childProperty.put("key_c_2", "val_c_2")
        property.put("child_key", childProperty)


        val attributes1 = HashMap<String, String>();
        attributes1.put("email", "examplex@gmail.com")

        val isEnabled: Boolean =
            MainApplication.optimizelyClient.isFeatureEnabled("samplefeatureforrudder", "sajal_mohanta", attributes1)
        if (isEnabled) {
            MainApplication.rudderClient.identify(
                "sajal_mohanta", RudderTraits()
                    .putName("User_id_identify")
                    .putEmail("identify@test.com")
                    .put("quantity", "5")
                    .put("price", "56.0")
                    .putAge("30")
                    .putBirthday("24th March 1990")
                    .putAddress(
                        RudderTraits.Address(
                            "KOlkata",
                            "India",
                            "700096",
                            "West bengal",
                            "Park Street"
                        )
                    )
                    .putCreatedAt(Date().toString())
                    .putDescription("Sample description")
                    .putFirstName("Sajal")
                    .putLastName("Mohanta")
                    .putBirthday("18th March 2020")
                    .putPhone("9112340345")
                    .putUserName("Samle_putUserName")
                , null
            )
            MainApplication.rudderClient.track("Application Opened", property)
        }

        MainApplication.rudderClient.track("Event2")

    }
}
