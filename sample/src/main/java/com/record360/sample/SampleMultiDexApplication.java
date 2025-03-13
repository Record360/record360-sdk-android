package com.record360.sample;

import static com.record360.sdk.Record360SDK.REGION_UNITED_STATES;
import static com.record360.sdk.Record360SDK.RESOLUTION_MEDIUM;
import static com.record360.sdk.Record360SDK.SETTING_ACCOUNT;
import static com.record360.sdk.Record360SDK.SETTING_COMPRESS_MEDIA;
import static com.record360.sdk.Record360SDK.SETTING_ENABLE_MULTIPLE_CAMERA;
import static com.record360.sdk.Record360SDK.SETTING_LEGACY_CAMERA;
import static com.record360.sdk.Record360SDK.SETTING_LICENSE_REGION;
import static com.record360.sdk.Record360SDK.SETTING_LINKS;
import static com.record360.sdk.Record360SDK.SETTING_LOGOUT;
import static com.record360.sdk.Record360SDK.SETTING_NATIVE_RESOLUTION;
import static com.record360.sdk.Record360SDK.SETTING_NOTATIONS_ON_IMAGES;
import static com.record360.sdk.Record360SDK.SETTING_RATE_RECORD360;
import static com.record360.sdk.Record360SDK.SETTING_RECORD_AUDIO;
import static com.record360.sdk.Record360SDK.SETTING_RESOLUTION;
import static com.record360.sdk.Record360SDK.SETTING_SEND_SUPPORT_LOG;
import static com.record360.sdk.Record360SDK.SETTING_SHOW_INTRO_VIDEO;
import static com.record360.sdk.Record360SDK.SETTING_TIMESTAMP_MODE;
import static com.record360.sdk.Record360SDK.SETTING_UPLOAD_MODE;
import static com.record360.sdk.Record360SDK.SETTING_VERSION;
import static com.record360.sdk.Record360SDK.SETTING_VIN_SCAN;
import static com.record360.sdk.Record360SDK.UPLOAD_MODE_ONLINE;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import com.record360.sdk.Record360SDK;
import com.record360.sdk.Record360Setting;


public class SampleMultiDexApplication extends MultiDexApplication {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Record360Setting[] settings = new Record360Setting[]{
                new Record360Setting(SETTING_UPLOAD_MODE, UPLOAD_MODE_ONLINE, true),
                new Record360Setting(SETTING_RESOLUTION, RESOLUTION_MEDIUM, true),
                new Record360Setting(SETTING_COMPRESS_MEDIA, Boolean.toString(true), true),
                new Record360Setting(SETTING_NOTATIONS_ON_IMAGES, Boolean.toString(false), true),
                new Record360Setting(SETTING_ENABLE_MULTIPLE_CAMERA, Boolean.toString(false), true),
                new Record360Setting(SETTING_LEGACY_CAMERA, Boolean.toString(false), true),
                new Record360Setting(SETTING_VIN_SCAN, Boolean.toString(false), true),
                new Record360Setting(SETTING_RECORD_AUDIO, Boolean.toString(true), true),
                new Record360Setting(SETTING_TIMESTAMP_MODE, Boolean.toString(true), true),
                new Record360Setting(SETTING_NATIVE_RESOLUTION, Boolean.toString(false), true),
                new Record360Setting(SETTING_LICENSE_REGION, REGION_UNITED_STATES, true),
                new Record360Setting(SETTING_SHOW_INTRO_VIDEO),
                new Record360Setting(SETTING_SEND_SUPPORT_LOG),
                new Record360Setting(SETTING_ACCOUNT),
                new Record360Setting(SETTING_LOGOUT),
                new Record360Setting(SETTING_LINKS, getString(com.record360.sdk.R.string.setting_access_records), "https://www.record360.com"),
                new Record360Setting(SETTING_RATE_RECORD360),
                new Record360Setting(SETTING_LINKS, getString(com.record360.sdk.R.string.setting_terms_of_service), "https://www.record360.com/terms"),
                new Record360Setting(SETTING_LINKS, getString(com.record360.sdk.R.string.setting_privacy_policy), "https://www.record360.com/privacy"),
                new Record360Setting(SETTING_VERSION)
        };

        Record360SDK.initialize(getApplicationContext(), settings);
    }

}
