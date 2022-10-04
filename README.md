[![Version](https://img.shields.io/badge/Record360SDK-4.9.2-success)](https://github.com/Record360/record360-sdk-android/packages/1655552)

Record360 Android SDK
==================

Last updated on – Oct 3rd, 2022

# Introduction

The Record360SDK is an Android Library that allows mobile clients to leverage [Record360](https://www.record360.com).  This allows the client to track and record the condition of assets using the Record360 workflow.  An account with Record360 is required.  Please contact sales@record360.com for details.

# Requirements

-   Android OS 5.0+ (API level 21)
-   ARM Architecture Device

# Example

To run the example project, clone the repo, and open in Android Studio. Click Run in IDE UI or run via the terminal by running 'gradlew assembleDebug' from the project directory.

# Installation with Gradle

Record360 SDK can be installed using Gradle. See example below.

### build.gradle

Modify your project (top-level) build.gradle file with the following lines.
Note: You will need to setup a Github PAT according to directions here:
https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages
Once you create PAT please insert your username and PAT in place of GITHUB_USERNAME and GITHUB_PAT
```groovy
    buildscript {
        repositories {
            jcenter()
            google()

        }

        dependencies {
            classpath 'com.android.tools.build:gradle:7.1.3'
            classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21'
        }
    }

    allprojects {
        repositories {
            jcenter()
            maven { url 'https://raw.githubusercontent.com/Acuant/AndroidSdkMaven/main/maven/' }
            maven { url 'https://raw.githubusercontent.com/iProov/android/master/maven/' }
            maven { url 'https://jitpack.io'}
            maven { url 'https://maven.google.com' }
            maven { url 'https://maven.pkg.github.com/Record360/record360-sdk-android'
                credentials {
                    username GITHUB_USERNAME
                    password GITHUB_PAT
                }
            }
        }
    }
```
Modify your App build.gradle (App Level) file in which you want to import the SDK with the following lines.
```groovy
    dependencies {
        implementation 'com.record360.sdk:android-sdk:4.9.2'
        kapt "com.google.dagger:dagger-compiler:2.44"
        implementation 'com.google.dagger:dagger:2.44'
        implementation 'androidx.multidex:multidex:2.0.1'
    }
```    

Modify your gradle.properties file to support androidx and jetifier
```groovy
    // Project-wide Gradle settings.
    android.enableJetifier=true
    android.useAndroidX=true
```    
    
Press the gradle sync button to import the SDK dependencies.

# Installation without Gradle

Record360 can provide the compiled code, contact support@record360.com for more information.

# Integrating Record360

### Enable Multidex
In order to properly and quickly build the application, MultiDex must be enabled.
Create a new java class in your package directory (e.g. app/java/com.example.sample/).
Also, statically initialize compat vectors for resources.
```java
    public class SampleMultiDexApplication extends MultidexApplication {
        static {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }
```
Add application class name to AndroidManifest.xml
```xml

	<application
	    android:name=".SampleMultiDexApplication">
      ...
	</application>
```
  
Add multidex flag to defaultConfig section in the module's build.gradle file
```groovy
	defaultConfig {
        multiDexEnabled true
	}
```

### Add Dagger
In order to use our library you must setup Dagger. You can copy and insert the class found at:
com.record360.sample.dagger.ApplicationComponent;
```java
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
        // You will have to create an inject method stub for your activity
        // MainActivity extends the library provided Record360Activity
        void inject(MainActivity launchActivity);
    }
```

# Enable access to generated Dagger class
```java

    public class SampleMultiDexApplication extends MultidexApplication {
        private static ApplicationComponent applicationComponent;

        public void onCreate() {
            super.onCreate();
            // ... 
            Record360SDK.initialize(getApplicationContext(), settings);
            initApplicationComponent(this);
        }

        public static void initApplicationComponent(Context appContext) {
            applicationComponent = DaggerApplicationComponent.builder()
            .contextModule(new ContextModule(appContext))
            .build();
        }

        public static ApplicationComponent getApplicationComponent() {
            return applicationComponent;
        }
    }
```

    
### Initialize the Record360SDK

Initialize a Record360SDK. This can be done at the Application level or within an Activity.
```java
    @Override
    public void onCreate() {
        super.onCreate();
        Record360SDK.Setting[] settings = ...
        Record360SDK.initialize(this, settings);
    }
```

### Entering the workflow

Create an Activity that will extend the Record360Activity. This will give you access to commands needed to enter the workflow.
Also, designate a Record360Interface that will handle SDK inspection events.
```java
    public class MainActivity extends Record360Activity implements Record360Interface {
        @Override
        public void onCreate() {
            start(this, this);
        }
    }
```
    
The session information will be sent from the SDK to the Record360Interface you supply as a parameter to the different start functions available in the Record360Activity. Above is an example of starting the workflow using our provided in login UI.

The start functions are as follows:
```java
    startWithLogin(Context context, @Nullable referenceNumber, Record360Interface record360Interface);
    authenticateAndStart(Context context, final String username, final String password, @Nullable referenceNumber, Record360Interface interface);
    authenticatedStart(Context context, final String userId, final String token, @Nullable final String referenceNumber, Record360Interface interface);
    authenticatedStart(Context context, String userId, String token, @Nullable String referenceNum, @Nullable Integer workOrderId, @Nullable String workOrderLabel, Record360Interface interface)
```
    
Depending on the state of the inspection in the workflow, the user will either be prompted to create a new inspection or resume their already existing inspection.

### Adding workflow settings
```java
	  Record360SDK.Setting[] sdkSettings = new Record360SDK.Setting[]{
                new Record360SDK.Setting(SETTING_NOTATIONS_ON_IMAGES, Boolean.toString(false), true),
                new Record360SDK.Setting(SETTING_VIN_SCAN, Boolean.toString(false), true),
                new Record360SDK.Setting(SETTING_NATIVE_RESOLUTION, Boolean.toString(false), true),
                new Record360SDK.Setting(SETTING_TIMESTAMP_MODE, Boolean.toString(true), true),
                new Record360SDK.Setting(SETTING_RESOLUTION, RESOLUTION_HIGH, true),
                new Record360SDK.Setting(SETTING_UPLOAD_MODE, UPLOAD_MODE_ONLINE, true),
                new Record360SDK.Setting(SETTING_SEND_SUPPORT_LOG),
                new Record360SDK.Setting(SETTING_LOGOUT),
                new Record360SDK.Setting(SETTING_RATE_RECORD360),
                new Record360SDK.Setting(SETTING_LINKS, "Privacy Policy", "https://www.record360.com/privacy"),
                new Record360SDK.Setting(SETTING_VERSION)
        };
```

### Responding to workflow events

Implement the Record360Interface to respond to workflow events.
To respond to user login events use the Record360Interface callback methods below.
```java
	public void onUserAuthenticated(final String username, final String userId, final String token);
	public void onInspectionCancelled(String referenceNumber);
```

After the inspection has finished or is cancelled by the user, one of the callback methods below will be called.
```java
	public void onInspectionComplete(String referenceNumber);
	public void onInspectionCancelled(String referenceNumber);
```
  
### Responding to inspection upload events

When the Record360SDK has finished uploading a inspection, on of the callback methods below will be called.
```java
	public void onInspectionUploaded(String referenceNumber);
	public void onInspectionUploadFailed(String referenceNumber);
```
  
Upload progress can also be monitored in the callback shown below.
```java
    public void onInspectionUploadProgress(String refNum, long complete, long total);
```

# Changelog
## Version 4.9.2
Brings SDK version up to match official Record360 Android App
Added Conditional fields
Improved upload progress UI
Added Tasks

## Version 1.7.1
Added 64bit Support

## Version 1.7
Added workflow versioning
Added ability to clear checklist fields based on inspection type

## Version 1.6.1
Fixed crashing bug that was caused by Record360 Settings that were not being saved to shared preferences correctly

## Version 1.6.0
Moved to new AndroidX support libraries
Requires new flags in gradle.properties file to tell IDE AndroidX will be used
    android.useAndroidX=true
    android.enableJetifier=true

## Version 1.5.1
Adds optional reference number parameter to SDK start functions.
    - Will start an inspection with reference number passed in once authenticated.

## Version 1.5
Updated gradle plugin version to 3.2.1
Updated Android build tools to 28


## Migrating from 1.3 -> 1.4
Record360Interface has been changed and requires changes to override functions.
The following call has been removed:

    Map<String, String> getTransactionData(final String referenceNumber, Map<String, String> transactionData)

The call will be replaced with the following:

    Map<String, String> onReferenceNumberEnteredWithFieldData(final String referenceNumber, Map<String, String> transactionData)

The following function was also added to allow mapping into new contract forms.

    Map<String, String> onContractFieldData(Map<String, String> contractData)

# License

See the LICENSE file for more info on the Record360SDK license.
