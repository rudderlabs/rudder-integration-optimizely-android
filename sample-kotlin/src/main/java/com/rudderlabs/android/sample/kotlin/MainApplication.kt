package com.rudderlabs.android.sample.kotlin

import android.app.Application
import com.optimizely.ab.android.sdk.OptimizelyClient
import com.optimizely.ab.android.sdk.OptimizelyManager
import com.rudderstack.android.integration.optimizely.OptimizelyIntegrationFactory
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
import com.rudderstack.android.sdk.core.RudderLogger

class MainApplication : Application() {
    companion object {
        private const val WRITE_KEY = "1f51mF0DoelkwziW0rKPG13PViw"
        private const val DATA_PLANE_URL = "https://be2c05f88c24.ngrok.io"
        private const val CONTROL_PLANE_URL = "https://api.dev.rudderlabs.com"
        lateinit var rudderClient: RudderClient
        lateinit var optimizelyClient: OptimizelyClient
    }

    override fun onCreate() {
        super.onCreate()
        val dataFile: String = "<YOUR DATAFILE's CONTENT"
        val om =  OptimizelyManager.builder()
            .withSDKKey("<YOUR SDK KEY>")
            .build(this)
        optimizelyClient = om.initialize(this, dataFile)
        rudderClient = RudderClient.getInstance(
            this,
            WRITE_KEY,
            RudderConfig.Builder()
                .withDataPlaneUrl(DATA_PLANE_URL)
                .withControlPlaneUrl(CONTROL_PLANE_URL)
                .withFactory(OptimizelyIntegrationFactory.FACTORY(om))
                .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                .build()
        )
    }
}
