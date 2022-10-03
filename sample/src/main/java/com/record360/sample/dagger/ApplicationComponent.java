package com.record360.sample.dagger;

import com.record360.sample.MainActivity;
import com.record360.sdk.dagger.AnalyticsModule;
import com.record360.sdk.dagger.ApiModule;
import com.record360.sdk.dagger.ConditionalModule;
import com.record360.sdk.dagger.ContextModule;
import com.record360.sdk.dagger.GsonModule;
import com.record360.sdk.dagger.InspectionModule;
import com.record360.sdk.dagger.NavigationModule;
import com.record360.sdk.dagger.NetworkModule;
import com.record360.sdk.dagger.NotificationModule;
import com.record360.sdk.dagger.RealmModule;
import com.record360.sdk.dagger.SettingsModule;
import com.record360.sdk.dagger.TemplateModule;
import com.record360.sdk.dagger.UploadModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                ConditionalModule.class,
                ContextModule.class,
                AnalyticsModule.class,
                ApiModule.class,
                GsonModule.class,
                NavigationModule.class,
                NetworkModule.class,
                RealmModule.class,
                NotificationModule.class,
                SettingsModule.class,
                TemplateModule.class,
                InspectionModule.class,
                UploadModule.class
        }
)

public interface ApplicationComponent {
    void inject(MainActivity mainActivity);
}

