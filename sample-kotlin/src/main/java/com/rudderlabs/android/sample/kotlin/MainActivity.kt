package com.rudderlabs.android.sample.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rudderstack.android.sdk.core.RudderMessageBuilder
import com.rudderstack.android.sdk.core.RudderProperty
import com.rudderstack.android.sdk.core.RudderTraits
import com.rudderstack.android.sdk.core.TrackPropertyBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendEvents()
    }

    private fun sendEvents() {

        MainApplication.rudderClient.screen(localClassName)

        val property = RudderProperty()
        property.put("key_1", "val_1")
        property.put("revenue", 20)

        val childProperty = RudderProperty()
        childProperty.put("key_c_1", "val_c_1")
        childProperty.put("key_c_2", "val_c_2")
        property.put("child_key", childProperty)

        MainApplication.rudderClient.identify(
            "test_user_idx",
            RudderTraits()
                .putEmail("examplex@gmail.com")
                .putFirstName("Foo")
                .putLastName("Bar")
                .putName("Ruchira"),
            null
        )

        val attributes = HashMap<String, String>();
        attributes.put("email", "examplex@gmail.com")

        val isEnabled: Boolean =
            MainApplication.optimizelyClient.isFeatureEnabled("testfeature", "test_user_idx", attributes)
        if (isEnabled) {
            MainApplication.rudderClient.track("New Event", property)
        }

        MainApplication.rudderClient.track("Event2")

    }
}
