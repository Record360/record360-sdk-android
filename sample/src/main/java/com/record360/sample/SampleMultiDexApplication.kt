package com.record360.sample

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.record360.sdk.R
import com.record360.sdk.Record360SDK.REGION_UNITED_STATES
import com.record360.sdk.Record360SDK.RESOLUTION_MEDIUM
import com.record360.sdk.Record360SDK.SETTING_ACCOUNT
import com.record360.sdk.Record360SDK.SETTING_LEGACY_CAMERA
import com.record360.sdk.Record360SDK.SETTING_LICENSE_REGION
import com.record360.sdk.Record360SDK.SETTING_LINKS
import com.record360.sdk.Record360SDK.SETTING_LOGOUT
import com.record360.sdk.Record360SDK.SETTING_NATIVE_RESOLUTION
import com.record360.sdk.Record360SDK.SETTING_NOTATIONS_ON_IMAGES
import com.record360.sdk.Record360SDK.SETTING_RATE_RECORD360
import com.record360.sdk.Record360SDK.SETTING_RECORD_AUDIO
import com.record360.sdk.Record360SDK.SETTING_RESOLUTION
import com.record360.sdk.Record360SDK.SETTING_SEND_SUPPORT_LOG
import com.record360.sdk.Record360SDK.SETTING_SHOW_INTRO_VIDEO
import com.record360.sdk.Record360SDK.SETTING_TIMESTAMP_MODE
import com.record360.sdk.Record360SDK.SETTING_UPLOAD_MODE
import com.record360.sdk.Record360SDK.SETTING_VERSION
import com.record360.sdk.Record360SDK.UPLOAD_MODE_ONLINE
import com.record360.sdk.Record360SDK.initialize
import com.record360.sdk.Record360Setting
import com.record360.sdk.ui.insets.EdgeToEdgeInitializer

class SampleMultiDexApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // OPTIONAL: for automatic android EdgeToEdge compatibility
        EdgeToEdgeInitializer.register(this)
        val settings: Array<Record360Setting> = arrayOf(
            Record360Setting(SETTING_UPLOAD_MODE, UPLOAD_MODE_ONLINE, true),
            Record360Setting(SETTING_RESOLUTION, RESOLUTION_MEDIUM, true),
            Record360Setting(SETTING_NOTATIONS_ON_IMAGES, false.toString(), true),
            Record360Setting(SETTING_LEGACY_CAMERA, false.toString(), true),
            Record360Setting(SETTING_RECORD_AUDIO, true.toString(), true),
            Record360Setting(SETTING_TIMESTAMP_MODE, true.toString(), true),
            Record360Setting(SETTING_NATIVE_RESOLUTION, false.toString(), true),
            Record360Setting(SETTING_LICENSE_REGION, REGION_UNITED_STATES, true),
            Record360Setting(SETTING_SHOW_INTRO_VIDEO),
            Record360Setting(SETTING_SEND_SUPPORT_LOG),
            Record360Setting(SETTING_ACCOUNT),
            Record360Setting(SETTING_LOGOUT),
            Record360Setting(SETTING_RATE_RECORD360),
            Record360Setting(
                SETTING_LINKS,
                getString(R.string.setting_terms_of_service),
                "https://www.record360.com/terms"
            ),
            Record360Setting(
                SETTING_LINKS,
                getString(R.string.setting_privacy_policy),
                "https://www.record360.com/privacy"
            ),
            Record360Setting(SETTING_VERSION)
        )

        initialize(getApplicationContext(), settings)
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
