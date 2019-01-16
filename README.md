[![Version](https://api.bintray.com/packages/record360/maven/record360-sdk/images/download.svg?version=1.5.1) ](https://bintray.com/record360/maven/record360-sdk/1.5.1/link)

Record360 Android SDK
==================

Last updated on – 01/11/2019

# Introduction

The Record360SDK is an Android Library that allows mobile clients to leverage [Record360](https://www.record360.com).  This allows the client to track and record the condition of assets using the Record360 workflow.  An account with Record360 is required.  Please contact sales@record360.com for details.

# Requirements

-   Android OS 4.4+ (API level 19)
-   ARM Architecture Device

# Example

To run the example project, clone the repo, and open in Android Studio. Click Run in IDE UI or run via the terminal by running 'gradlew assembleDebug' from the project directory.

# Installation with Gradle

Record360 SDK can be installed using Gradle. Gradle uses JCenter and Google as its dependency managers that automate and simplify the process of integrating 3rd-party libraries into your projects. It also allows you to specify maven urls to pull in repositories that may not be hosted on JCenter or Google.

### build.gradle

Modify your projects build.gradle file with the following lines.

    buildscript {
        repositories {
            jcenter()
        }

        dependencies {
            classpath 'com.android.tools.build:gradle:3.2.1'
        }
    }

    allprojects {
        repositories {
            jcenter() // default
            maven { url 'http://dl.bintray.com/record360/maven'}
            maven { url 'http://maven.microblink.com' }
            maven { url 'http://maven.google.com' }
        }
    }

Modify your module build.gradle file in which you want to import the SDK with the following lines.
    
    dependencies {
        compile 'com.record360.sdk:android-sdk:1.5.1'
    }
    
Press the gradle sync button to import the SDK dependencies.

# Installation without Gradle

Record360 can provide the compiled code, contact support@record360.com for more information.

# Integrating Record360

### Enable Multidex
In order to properly and quickly build the application, MultiDex must be enabled.
Create a new java class in your package directory (e.g. app/java/com.example.sample/).
Also, statically initialize compat vectors for resources.

    public class SampleMultiDexApplication extends MultidexApplication {
        static {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }
    
Add application class name to AndroidManifest.xml

	<application
	    android:name=".SampleMultiDexApplication"
      ...
	</application>
  
Add multidex flag to defaultConfig section in the module's build.gradle file

	defaultConfig {
      multiDexEnabled true
	}
    
### Initialize the Record360SDK

Initialize a Record360SDK. This can be done at the Application level or within an Activity.
    
    @Override
    public void onCreate() {
        super.onCreate();
        Record360SDK.Setting[] settings = ...
        Record360SDK.initialize(this, settings);
    }

### Entering the workflow

Create an Activity that will extend the Record360Activity. This will give you access to commands needed to enter the workflow.
Also, designate a Record360Interface that will handle SDK transaction events.

    public class MainActivity extends Record360Activity implements Record360Interface {
        public void OnCreate() {
            ...
            start(this, this);
        }
    }
    
The session information will be sent from the SDK to the Record360Interface you supply as a parameter to the different start functions available in the Record360Activity. Above is an example of starting the workflow using our provided in login UI.

The start functions are as follows:

    public void start(Context context, Record360Interface record360Interface);
    public void authenticateAndStart(Context context, final String username, final String password, Record360Interface interface);
    public void authenticatedStart(Context context, final String token, @Nullable final String refNum, final String userId, Record360Interface interface);
    
Depending on the state of the transaction in the workflow, the user will either be prompted to create a new transaction or resume their already existing transaction.

### Adding workflow settings
	
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

### Responding to workflow events

Implement the Record360Interface to respond to workflow events.
To respond to user login events use the Record360Interface callback methods below.

	public void onUserAuthenticated(final String username, final String userId, final String token);
	public void onTransactionCancelled(String referenceNumber);

After the transaction has finished or is cancelled by the user, one of the callback methods below will be called.
	
	public void onTransactionComplete(String referenceNumber);
	public void onTransactionCancelled(String referenceNumber);
  
### Responding to transaction upload events

When the Record360SDK has finished uploading a transaction, on of the callback methods below will be called.

	public void onTransactionUploaded(String referenceNumber);
	public void onTransactionUploadFailed(String referenceNumber);
  
Upload progress can also be monitored in the callback shown below.

	public void onTransactionUploadProgress(String refNum, long complete, long total);

Please see the detailed instructions in our [SDK documentation](https://github.com/Record360/record360-sdk-android/blob/master/SDK.pdf)

# Changelog
## Version 1.5.1
Adds optional reference number parameter to SDK start functions.
    - Will start a transaction with reference number passed in once authenticated.

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
