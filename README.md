[![Version](https://img.shields.io/badge/Record360SDK-4.30.2-success)](https://github.com/Record360/record360-sdk-android/packages/1655552?version=4.30.2)

Record360 Android SDK
==================

Last updated on – September 25th, 2026

# Introduction

The Record360SDK is an Android Library that allows mobile clients to embed [Record360](https://www.record360.com). This allows the client to track and record the condition of assets using the Record360 workflow. **An account with Record360 is required.  Please contact sales@record360.com for details.**

# Requirements

-   Android OS 8.0+ (API level 26)
-   ARM Architecture Device
-   Kotlin Gradle plugin 2.1 or newer (2.2.0 recommended). The SDK is compiled with Kotlin 2.2 and older plugins cannot read its metadata.
-   Android Gradle plugin 8.11.2 or newer

# Example

To run the example project, clone the repo, and open in Android Studio. Click Run in IDE UI or run via the terminal by running './gradlew assembleDebug' from the project directory.

# Installation with Gradle

Record360 SDK can be installed using Gradle. See example below.

### build.gradle

Modify your project (top-level) build.gradle file with the following lines.
Note: You will need to setup a Github PAT according to directions here:
https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages
Once you create PAT please insert your username and PAT in place of GITHUB_USERNAME and GITHUB_PAT (the example below makes use of global gradle properties to save sensitive data and keep it out of source control)
```groovy
    buildscript {
        repositories {
            google()
            mavenCentral()
        }

        dependencies {
            classpath 'com.android.tools.build:gradle:8.11.2'
            classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.0'
        }
    }

    allprojects {
        repositories {
            mavenCentral()
            maven { url 'https://maven.google.com' }
            maven { url 'https://maven.scijava.org/content/repositories/public/' }
            maven { url 'https://raw.githubusercontent.com/iProov/android/master/maven/' }
            maven { url 'https://raw.githubusercontent.com/Acuant/AndroidSdkMaven/main/maven/' }
            maven { url 'https://jitpack.io'}
            maven { url 'https://my.nutrient.io/maven/' }
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
    android {
        defaultConfig {
            manifestPlaceholders = [
                    appAuthRedirectScheme: "sample",
            ]
        }
    
        packagingOptions {
            jniLibs {
                pickFirsts += ['lib/x86/libc++_shared.so', 'lib/x86_64/libc++_shared.so', 'lib/armeabi-v7a/libc++_shared.so', 'lib/arm64-v8a/libc++_shared.so']
                excludes += ["**/libconceal.so"]
                useLegacyPackaging = true
            }
        }
    
        compileOptions {
            sourceCompatibility JavaVersion.VERSION_17
            targetCompatibility JavaVersion.VERSION_17
            coreLibraryDesugaringEnabled true
        }
    
        kotlinOptions {
            jvmTarget = "17"
        }
        
        dependencies {
            implementation 'com.record360.sdk:android-sdk:4.30.2'
            implementation 'androidx.multidex:multidex:2.0.1'
            coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.0.4'
        }
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

# Initialize the Record360SDK
Initialize a Record360SDK. This can be done at the Application level or within an Activity.
```java

public class SampleMultiDexApplication extends MultidexApplication {
    public void onCreate() {
        super.onCreate();
        // OPTIONAL: for automatic android EdgeToEdge compatibility
        // Use if SDK views are overlapping with status bars
        EdgeToEdgeInitializer.INSTANCE.register(this);
        Record360SDK.Setting[] settings = ...
        Record360SDK.initialize(getApplicationContext(), settings);
    }
}
```
```kotlin
class SampleMultiDexApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // OPTIONAL: for automatic android EdgeToEdge compatibility
        // Use if SDK views are overlapping with status bars
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
            Record360Setting(SETTING_VERSION)
        )

        initialize(getApplicationContext(), settings)
    }
}
```

### Entering the workflow

Create an Activity that will extend the Record360Activity. This will give you access to commands needed to enter the workflow.
Also, designate a Record360Interface that will handle SDK inspection events.
You can implement this on an Activity that launches our SDK or an application object
```kotlin
class MainActivity : Record360Activity(), IRecord360Interface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Record360SDK.r360Interface = this
    }

    override fun onDestroy() {
        super.onDestroy()
        Record360SDK.r360Interface = null
    }
}
```
    
The session information will be sent from the SDK to the Record360Interface you supply as a parameter to the different start functions available in the Record360Activity. Above is an example of starting the workflow using our provided in login UI.

The start functions are as follows:
JAVA
```java
startRecord360(Context context, @Nullable String referenceNumber, @Nullable String department, @Nullable String sequence, @Nullable String loginUsername, @Nullable Map<String, Object> fieldData);
startRecord360WithIdentity(Context context, R360Identity r360Identity, @Nullable String referenceNumber, @Nullable String department, @Nullable String sequence, @Nullable String loginUsername, @Nullable Map<String, Object> fieldData);
startWithTaskId(Context context, @Nullable String taskId);
```
KOTLIN
```kotlin
fun startRecord360(
        context: Context,
        referenceNumber: String?,
        department: String?,
        sequence: String?,
        loginUsername: String?,
        fieldData: Map<String, Any?>? = null
    )
fun startRecord360WithIdentity(
    context: Context,
    r360Identity: R360Identity,
    referenceNumber: String?,
    department: String?,
    sequence: String?,
    fieldData: Map<String, Any?>? = null
)
fun startWithTaskId(context: Context, taskId: String?)
```
The addition of fieldData will try to match map keys to checklist field ids or labels
If the keys match and value is valid for checklist field, data should map automatically
*note* any field data that does not match will be silently ignored

In order to pass in credentials when starting Record360, you must now use the class R360Identity
```java
    val passwordIdentity = new R360Identity.PasswordIdentity(username, password);
    val tokenIdentity = new R360Identity.UserTokenIdentity(userId, userToken);
```
    
Depending on the state of the inspection in the workflow, the user will either be prompted to create a new inspection or resume their already existing inspection.

### Adding workflow settings
```java
	  Record360Setting[] sdkSettings = new Record360Setting[]{
                new Record360Settingg(SETTING_NOTATIONS_ON_IMAGES, Boolean.toString(false), true),
                new Record360Setting(SETTING_NATIVE_RESOLUTION, Boolean.toString(false), true),
                new Record360Setting(SETTING_TIMESTAMP_MODE, Boolean.toString(true), true),
                new Record360Setting(SETTING_RESOLUTION, RESOLUTION_HIGH, true),
                new Record360Setting(SETTING_UPLOAD_MODE, UPLOAD_MODE_ONLINE, true),
                new Record360Setting(SETTING_SEND_SUPPORT_LOG),
                new Record360Setting(SETTING_LOGOUT),
                new Record360Setting(SETTING_RATE_RECORD360),
                new Record360Setting(SETTING_LINKS, "Privacy Policy", "https://www.record360.com/privacy"),
                new Record360Setting(SETTING_VERSION)
        };
```

### Responding to workflow events

Implement the IRecord360Interface to respond to SDK events. Every inspection event carries the unit's reference number and an `inspectionNonce`: an opaque id that is unique per inspection and identical across all of that inspection's events, from `onInspectionStarted` through `onInspectionUploaded`. Reference numbers repeat across inspections of the same unit, so use the nonce to correlate.

Authentication:
```java
	public void onSuccessfulAuthentication(String userToken, String userId);
	public void onFailedAuthentication(Throwable error);
```

Inspection lifecycle. `onInspectionStarted` fires once the inspection has been created (including a resumed draft). One of `onInspectionComplete` or `onInspectionCancelled` follows. Cancelled fires whenever an in-progress inspection is discarded: the user deleted it, or it was cleared by a logout, an expired session, or a new launch for a different reference number or user.
```java
	public void onInspectionStarted(String refNum, String inspectionNonce);
	public void onInspectionComplete(String refNum, String inspectionNonce);
	public void onInspectionCancelled(String refNum, String inspectionNonce);
```

SDK lifecycle. Exactly one of these fires per launch, after any inspection event, once the SDK's screens have finished and control is back with your app. A launch whose supplied identity fails to authenticate never shows an SDK screen; it reports `onSDKShouldExit` once the login error dialog is dismissed.
```java
	public void onSDKShouldExit();
	public void onSDKShouldRelaunch();
```
`onSDKShouldRelaunch` is only sent by the iOS SDK today; it is declared here so hosts can share one implementation across platforms. Both have default no-op implementations.

### Responding to inspection upload events

Uploads run in the background after `onInspectionComplete`, and continue if your app is backgrounded or restarted. When an upload finishes, one of the callback methods below will be called.
```java
	public void onInspectionUploaded(String refNum, String inspectionNonce, String inspectionJSON);
	public void onInspectionUploadFailed(String refNum, String inspectionNonce, String error);
	public void onInspectionUploadDeleted(String refNum, String inspectionNonce);
```
A failed upload stays queued and is retried automatically, except when media files are missing on the device. `onInspectionUploadDeleted` fires when the user removes a queued upload from the Uploads screen. `onInspectionUploadDeleted`, `onInspectionStarted`, `onSDKShouldExit` and `onSDKShouldRelaunch` have default implementations, so implement only the ones you need.

Here is an example inspectionJson response:
```json
{
  "id": "430",
  "type": "open",
  "unit": {
    "id": "43",
    "reference_number": "WMWRF33519TW73378"
  },
  "damage_present": false,
  "dashboard_url": "https://dashboard.record360.com/dashboard/inspections/123",
  "pdf_share_url": "https://app.record360.com/exports/30Ew8II5bNYaDwXt?code=123",
  "user": {
    "id": "120",
    "username": "jdoe@record360.com"
  },
  "location": {
    "id": "220",
    "external_id": null,
    "name": "Denver Branch"
  },
  "region": {
    "id": "1",
    "name": "Mountain West"
  },
  "department": {
    "id": "1",
    "name": "Rental"
  },
  "results": [
    {
      "name": "Basic Checklist",
      "object": "checklist",
      "data": [
        {
          "id": "odom1",
          "type": "textbox",
          "label": "Mileage",
          "value": 120000
        },
        {
          "id": "fuel1",
          "type": "single_dropdown_list",
          "label": "Fuel level",
          "value": "7/8"
        },
        {
          "type": "textbox",
          "label": "Customer Name",
          "value": "John Doe"
        },
        {
          "type": "date_selector",
          "label": "Inspection Date",
          "value": "2025-01-31"
        },
        {
          "type": "checkbox",
          "label": "Attachments",
          "items": [
            {
              "id": "rake1",
              "label": "Rake Riddle",
              "value": "true"
            },
            {
              "id": "bucket1",
              "label": "Quick Attach Bucket",
              "value": "false"
            }
          ]
        },
        {
          "id": "mpi1",
          "type": "pass_fail",
          "label": "Service Walkthrough",
          "items": [
            {
              "id": "HLF1",
              "label": "Headlights",
              "value": "Pass"
            },
            {
              "id": "BLR2",
              "label": "Brake Lights",
              "value": "Fail"
            }
          ]
        }
      ]
    },
    {
      "name": "Rental Contract",
      "object": "pdf_form",
      "link": "https://app.record360.com/v3/forms/1WwmZX857Zb6ASpv",
      "data": {
        "one": "two"
      }
    },
    {
      "name": "Collision Damage Waiver",
      "object": "collision_damage_waiver",
      "accepted": false
    }
  ],
  "photos": [
    {
      "object": "photo",
      "url": "https://media.record360.com/inspections/430/images/1c4bdd32-aa80-4355-a6aa-c56ed4c4102d",
      "instruction": {
        "id": "abc123",
        "label": "Take a photo of the left side"
      },
      "field": null
    }
  ],
  "videos": [
    {
      "object": "video",
      "url": "https://media.record360.com/inspections/430/videos/5f8a21e9-8600-4b73-9d27-e296c83e939a"
    }
  ],
  "signatures": [
    {
      "object": "signature",
      "type": "inspection",
      "label": "Signed By",
      "printed_name": "John Doe",
      "url": "https://media.record360.com/inspections/430/signatures/61b5af9d-b399-4113-8bb0-728f30f12424"
    },
    {
      "object": "signature",
      "type": "esign",
      "signed_at": "2026-01-30T06:55:31.441Z",
      "url": "https://media.record360.com/inspections/430/signatures/61b5af9d-b399-4113-8bb0-728f30f12424"
    }
  ],
  "created_at": "2014-04-01T19:00:00.000Z",
  "updated_at": "2014-04-01T19:00:00.000Z"
}
```
  
Upload progress can also be monitored in the callback shown below. `percentComplete` is a fraction from 0.0 to 1.0 of media bytes uploaded, reported about once per second while an upload is active, and never after `onInspectionUploaded` or `onInspectionUploadDeleted` for that nonce.
```java
    public void onInspectionUploadProgress(String refNum, String inspectionNonce, double percentComplete);
```

# Changelog
## Version 4.30.2 (October 9th, 2026)
- `onSDKShouldExit` now fires after the login error dialog is dismissed when the identity passed to `startRecord360WithIdentity` is rejected. Previously no exit was reported because no SDK screen had opened, leaving the host activity on screen.

## Version 4.30.1 (September 25th, 2026)
- Requirements changed (updates needed)
  - Minimum Android version is now 8.0 (API level 26)
  - Kotlin Gradle plugin 2.1 or newer is required; the SDK is compiled with Kotlin 2.2 (update the classpath in your project-level build.gradle, see above)
- Breaking interface changes to IRecord360Interface: every inspection callback now carries the reference number and an `inspectionNonce` that is stable across all of one inspection's events
  ```java
  // OLD                                                                     // NEW
  onUserAuthenticated(String username, String userId, String token)         onSuccessfulAuthentication(String userToken, String userId)
  onFailedToAuthenticate(boolean credentialsAreValid, String error)         onFailedAuthentication(Throwable error)
  onInspectionComplete(String referenceNumber)                              onInspectionComplete(String refNum, String inspectionNonce)
  onInspectionCancelled(String referenceNumber)                             onInspectionCancelled(String refNum, String inspectionNonce)
  onInspectionUploadProgress(String refNum, long complete, long total)      onInspectionUploadProgress(String refNum, String inspectionNonce, double percentComplete)
  onInspectionUploaded(String referenceNumber, String inspectionJson)       onInspectionUploaded(String refNum, String inspectionNonce, String inspectionJSON)
  onInspectionUploadFailed(String referenceNumber, String error)            onInspectionUploadFailed(String refNum, String inspectionNonce, String error)
  ```
- New callbacks (all with default no-op implementations)
  - `onInspectionStarted(refNum, inspectionNonce)` once an inspection has been created
  - `onInspectionUploadDeleted(refNum, inspectionNonce)` when the user removes a queued upload from the Uploads screen
  - `onSDKShouldExit()` exactly once per launch, when the SDK hands control back to your app
  - `onSDKShouldRelaunch()` declared for parity with the iOS SDK; not sent on Android
- Behavior changes
  - `onInspectionCancelled` now fires on every discard of an in-progress inspection, including logout, session expiry, and launching for a different reference number or user, not only the delete dialog
  - `onInspectionUploaded` now passes the reference number; it previously passed an internal upload id
  - `onInspectionUploadFailed` now carries the actual cause instead of a fixed message
  - Upload progress is a 0.0 to 1.0 fraction and stops once an upload completes or is deleted; previously a final 0 of N could be reported after completion

## Version 4.26.1 (June 22nd, 2026)
- Bug fixes and performance improvements
- License Scanner Fix

## Version 4.26.0 (June 15th, 2026)
- Update targetSdk and compileSdk to 36
- Breaking Change to onInspectionUploaded call back
  - Will now receive new format for inspectionJson (see above for example)
- Start functions updated
  - New field data parameter on start functions that will allow automapping to specified fields that match checklist items
- Update to build.gradle repository url (changes needed)
```groovy
// OLD
maven { url 'https://customers.pspdfkit.com/maven/' }
// NEW
maven { url 'https://my.nutrient.io/maven/' }
```

## Version 4.25.3 (April 30, 2026)
- Start functions updated
  - Work Order Parameters removed due to deprecation
  - Department id or name now supported as a parameter
  - Sequence id or name now supported as a parameter
- New SDK Items
  - Bug fixes
  - Improved Integrations

## Version 4.24.0 (Feb 11th, 2026)
- Upgraded Build Tools Gradle Version to 8.11.2
- Upgraded Android Target SDK Version to 35
- Added EdgeToEdgeInitializer class to support Android API Level 35+
- Please update your default config with the following placeholder
```groovy
defaultConfig {
    manifestPlaceholders = [
        appAuthRedirectScheme: "sample",
    ]
}
```
- 16KB alignment (Please update your build.gradle file with new packaging options)
```groovy
    packagingOptions {
        jniLibs {
            pickFirsts += ['lib/x86/libc++_shared.so', 'lib/x86_64/libc++_shared.so', 'lib/armeabi-v7a/libc++_shared.so', 'lib/arm64-v8a/libc++_shared.so']
            excludes += ["**/libconceal.so"]
            useLegacyPackaging = true
        }
    }
```
- New SDK items
    - License Plate Scanner
    - User Groups

## Version 4.19.3 (July 25th, 2025)
- Breaking interface change
  - callback function onInspectionUploaded now returns inspection Json data
    ```java
	    public void onInspectionUploaded(String referenceNumber, String inspectionJson);
    ```
- New SDK items
  - Wide Angle Camera Support

## Version 4.19.1 (March 17th, 2025)
- Breaking interface changes
    - Updates to start functions
    - Addition of R360Identity
    - Record360SDK.Setting is now Record360Setting
    - Record360SDK.Record360Interface is now IRecord360Interface

## Version 4.18-5 (March 12th, 2025)
-  New SDK Items
  -  Fixed task issue that was incorrectly pulling draft data

## Version 4.18-4 (Mar 10th, 2025)
-  Update Android Gradle Plugin to 8.5.2
-  Update Kotlin Gradle Plugin to 2.0.21
-  New SDK Features
  - Improvements to PDF signatures
  - Improved task handling
  - Improved settings
  - Improved Camera UI

## Version 4.16.3-1 (Aug 14th, 2024)
-   Fix SDK exit routing
-   Fix some compilation issues when used in external projects

## Version 4.16.1-5 (Jul 11th, 2024)
-   Updated Target Android SDK to 34
-   Fixes sdk exit routing when user completes or cancels an inspection with provided ref num

## Version 4.15.7-1 (May 8th, 2024)
-   Upgraded Android Gradle Plugin to 8.2.2
-   Upgraded Kotlin Gradle Plugin to 1.8.10
-   Updated JVM to VERSION_17
-   CoreLibraryDesugaring now needed in host app

## Version 4.15.2
-   Android minimum SDK verion bumped up to Android 7.0 (24+)
-   Removed Dagger Requirement in host app
-   Upgraded Android Gradle Plugin to 7.4.2
-   Updated JVM to Version_11

## Version 4.9.4
-   Fixes dependency issues
-   Note: Make sure to update (project-level) build.gradle file as shown above

## Version 4.9.3
-   Fixes crashing issue in TaskDetailActivity

## Version 4.9.2
-   Brings SDK version up to match official Record360 Android App
-   Added Conditional fields
-   Improved upload progress UI
-   Added Tasks

## Version 1.7.1
-   Added 64bit Support

## Version 1.7
-   Added workflow versioning
-   Added ability to clear checklist fields based on inspection type

## Version 1.6.1
-   Fixed crashing bug that was caused by Record360 Settings that were not being saved to shared preferences correctly

## Version 1.6.0
-   Moved to new AndroidX support libraries
-   Requires new flags in gradle.properties file to tell IDE AndroidX will be used
    -   android.useAndroidX=true
    -   android.enableJetifier=true

## Version 1.5.1
-   Adds optional reference number parameter to SDK start functions.
    - Will start an inspection with reference number passed in once authenticated.

## Version 1.5
-   Updated gradle plugin version to 3.2.1
-   Updated Android build tools to 28


## Migrating from 1.3 -> 1.4
-   Record360Interface has been changed and requires changes to override functions.
The following call has been removed:

    Map<String, String> getTransactionData(final String referenceNumber, Map<String, String> transactionData)

The call will be replaced with the following:

    Map<String, String> onReferenceNumberEnteredWithFieldData(final String referenceNumber, Map<String, String> transactionData)

The following function was also added to allow mapping into new contract forms.

    Map<String, String> onContractFieldData(Map<String, String> contractData)

# License

See the LICENSE file for more info on the Record360SDK license.
