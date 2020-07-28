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
        private const val WRITE_KEY = "1dnyWDUi2IILwIS1KlfeikjZL0R"
        private const val DATA_PLANE_URL = "https://505d04e196c1.ngrok.io"
        private const val CONTROL_PLANE_URL = "https://api.dev.rudderlabs.com"
        lateinit var rudderClient: RudderClient
        lateinit var optimizelyClient: OptimizelyClient
    }

    override fun onCreate() {
        super.onCreate()
        val dataFile: String = "{\"version\": \"4\", \"rollouts\": [{\"experiments\": [{\"status\": \"Running\", \"audienceIds\": [], \"variations\": [{\"variables\": [], \"id\": \"18446710135\", \"key\": \"18446710135\", \"featureEnabled\": true}], \"id\": \"18468011049\", \"key\": \"18468011049\", \"layerId\": \"18392901783\", \"trafficAllocation\": [{\"entityId\": \"18446710135\", \"endOfRange\": 10000}], \"forcedVariations\": {}}], \"id\": \"18392901783\"}, {\"experiments\": [{\"status\": \"Running\", \"audienceConditions\": [\"or\", \"18396823465\"], \"audienceIds\": [\"18396823465\"], \"variations\": [{\"variables\": [], \"id\": \"18441811837\", \"key\": \"18441811837\", \"featureEnabled\": true}], \"id\": \"18417461690\", \"key\": \"18417461690\", \"layerId\": \"18398642807\", \"trafficAllocation\": [{\"entityId\": \"18441811837\", \"endOfRange\": 0}], \"forcedVariations\": {}}, {\"status\": \"Running\", \"audienceIds\": [], \"variations\": [{\"variables\": [], \"id\": \"18437811607\", \"key\": \"18437811607\", \"featureEnabled\": true}], \"id\": \"18429842634\", \"key\": \"18429842634\", \"layerId\": \"18398642807\", \"trafficAllocation\": [{\"entityId\": \"18437811607\", \"endOfRange\": 4700}], \"forcedVariations\": {}}], \"id\": \"18398642807\"}], \"typedAudiences\": [{\"id\": \"18396823465\", \"conditions\": [\"and\", [\"or\", [\"or\", {\"value\": null, \"type\": \"custom_attribute\", \"name\": \"userId\", \"match\": \"exists\"}]]], \"name\": \"RudderAudience1\"}], \"anonymizeIP\": true, \"projectId\": \"18440940489\", \"variables\": [], \"featureFlags\": [{\"experimentIds\": [\"18442611518\"], \"rolloutId\": \"18392901783\", \"variables\": [], \"id\": \"18404322613\", \"key\": \"samplefeatureforrudder\"}, {\"experimentIds\": [], \"rolloutId\": \"18398642807\", \"variables\": [], \"id\": \"18421462108\", \"key\": \"samplerudderfeature1\"}], \"experiments\": [{\"status\": \"Running\", \"audienceIds\": [], \"variations\": [{\"variables\": [], \"id\": \"18447092297\", \"key\": \"variation_1\", \"featureEnabled\": true}, {\"variables\": [], \"id\": \"18487360189\", \"key\": \"variation_2\", \"featureEnabled\": true}], \"id\": \"18442611518\", \"key\": \"samplefeatureforrudder_test\", \"layerId\": \"18445242667\", \"trafficAllocation\": [{\"entityId\": \"18447092297\", \"endOfRange\": 5000}, {\"entityId\": \"18487360189\", \"endOfRange\": 10000}], \"forcedVariations\": {}}], \"audiences\": [{\"id\": \"18396823465\", \"conditions\": \"[\\\"or\\\", {\\\"match\\\": \\\"exact\\\", \\\"name\\\": \\\"\$opt_dummy_attribute\\\", \\\"type\\\": \\\"custom_attribute\\\", \\\"value\\\": \\\"\$opt_dummy_value\\\"}]\", \"name\": \"RudderAudience1\"}, {\"conditions\": \"[\\\"or\\\", {\\\"match\\\": \\\"exact\\\", \\\"name\\\": \\\"\$opt_dummy_attribute\\\", \\\"type\\\": \\\"custom_attribute\\\", \\\"value\\\": \\\"\$opt_dummy_value\\\"}]\", \"id\": \"\$opt_dummy_audience\", \"name\": \"Optimizely-Generated Audience for Backwards Compatibility\"}], \"groups\": [], \"attributes\": [{\"id\": \"18396902405\", \"key\": \"userId\"}, {\"id\": \"18398562177\", \"key\": \"price\"}, {\"id\": \"18400562390\", \"key\": \"description\"}, {\"id\": \"18406051643\", \"key\": \"firstName\"}, {\"id\": \"18416311472\", \"key\": \"birthday\"}, {\"id\": \"18420152407\", \"key\": \"phone\"}, {\"id\": \"18429832345\", \"key\": \"age\"}, {\"id\": \"18440241687\", \"key\": \"address\"}, {\"id\": \"18441972196\", \"key\": \"lastName\"}, {\"id\": \"18455012267\", \"key\": \"userName\"}, {\"id\": \"18477401053\", \"key\": \"email\"}, {\"id\": \"18483580406\", \"key\": \"quantity\"}], \"botFiltering\": false, \"accountId\": \"18440940489\", \"events\": [{\"experimentIds\": [], \"id\": \"18421822738\", \"key\": \"Application Backgrounded\"}, {\"experimentIds\": [], \"id\": \"18427242905\", \"key\": \"level_up\"}, {\"experimentIds\": [], \"id\": \"18433452544\", \"key\": \"daily_rewards_claim\"}, {\"experimentIds\": [], \"id\": \"18437592480\", \"key\": \"revenue\"}, {\"experimentIds\": [], \"id\": \"18439173025\", \"key\": \"Event2\"}, {\"experimentIds\": [\"18442611518\"], \"id\": \"18490990119\", \"key\": \"Application Opened\"}], \"revision\": \"43\"}"
        val om =  OptimizelyManager.builder()
            .withSDKKey("SWGMW1LFABX2Q8LTyk5uN")
            .build(this)
        optimizelyClient = om.initialize(this , dataFile)
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
