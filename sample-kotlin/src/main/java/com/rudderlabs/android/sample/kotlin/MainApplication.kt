package com.rudderlabs.android.sample.kotlin

import android.app.Application
import com.rudderstack.android.integration.optimizely.OptimizelyIntegrationFactory
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
import com.rudderstack.android.sdk.core.RudderLogger

class MainApplication : Application() {
    companion object {
        lateinit var rudderClient: RudderClient
    }

    override fun onCreate() {
        super.onCreate()
        rudderClient = RudderClient.getInstance(
            this,
            "1f5zqExMWp0qLrapr8RCAl5n9Tu",
            RudderConfig.Builder()
                .withDataPlaneUrl("https://7866cc22.ngrok.io")
                .withLogLevel(RudderLogger.RudderLogLevel.DEBUG)
                .withFactory(OptimizelyIntegrationFactory.FACTORY)
                .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                .build()
        )
    }
}
