package com.record360.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.record360.sdk.Record360Activity;
import com.record360.sdk.Record360SDK;

import java.util.Map;

import static com.record360.sdk.Record360SDK.REGION_UNITED_STATES;
import static com.record360.sdk.Record360SDK.RESOLUTION_HIGH;
import static com.record360.sdk.Record360SDK.SETTING_ACCOUNT;
import static com.record360.sdk.Record360SDK.SETTING_LICENSE_REGION;
import static com.record360.sdk.Record360SDK.SETTING_LINKS;
import static com.record360.sdk.Record360SDK.SETTING_LOGOUT;
import static com.record360.sdk.Record360SDK.SETTING_NATIVE_RESOLUTION;
import static com.record360.sdk.Record360SDK.SETTING_NOTATIONS_ON_IMAGES;
import static com.record360.sdk.Record360SDK.SETTING_RATE_RECORD360;
import static com.record360.sdk.Record360SDK.SETTING_RESOLUTION;
import static com.record360.sdk.Record360SDK.SETTING_SEND_SUPPORT_LOG;
import static com.record360.sdk.Record360SDK.SETTING_TIMESTAMP_MODE;
import static com.record360.sdk.Record360SDK.SETTING_UPLOAD_MODE;
import static com.record360.sdk.Record360SDK.SETTING_VERSION;
import static com.record360.sdk.Record360SDK.SETTING_VIN_SCAN;
import static com.record360.sdk.Record360SDK.UPLOAD_MODE_ONLINE;

public class MainActivity extends Record360Activity implements Record360Activity.Record360Interface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Record360SDK.Setting[] sdkSettings = new Record360SDK.Setting[]{
                new Record360SDK.Setting(SETTING_NOTATIONS_ON_IMAGES, Boolean.toString(false), true),
                new Record360SDK.Setting(SETTING_VIN_SCAN, Boolean.toString(false), true),
                new Record360SDK.Setting(SETTING_NATIVE_RESOLUTION, Boolean.toString(false), true),
                new Record360SDK.Setting(SETTING_TIMESTAMP_MODE, Boolean.toString(true), true),
                new Record360SDK.Setting(SETTING_RESOLUTION, RESOLUTION_HIGH, true),
                new Record360SDK.Setting(SETTING_UPLOAD_MODE, UPLOAD_MODE_ONLINE, true),
                new Record360SDK.Setting(SETTING_LICENSE_REGION, REGION_UNITED_STATES, true),
                new Record360SDK.Setting(SETTING_SEND_SUPPORT_LOG),
                new Record360SDK.Setting(SETTING_ACCOUNT),
                new Record360SDK.Setting(SETTING_LOGOUT),
                new Record360SDK.Setting(SETTING_LINKS, "Access records", "https://www.record360.com"),
                new Record360SDK.Setting(SETTING_RATE_RECORD360),
                new Record360SDK.Setting(SETTING_LINKS, "Terms of Service", "https://www.record360.com/terms"),
                new Record360SDK.Setting(SETTING_LINKS, "Privacy Policy", "https://www.record360.com/privacy"),
                new Record360SDK.Setting(SETTING_VERSION)
        };

        // Initialize the Record360SDK
        Record360SDK.initialize(this, sdkSettings);

        EditText usernameEditText = findViewById(R.id.sample_username);
        EditText passwordEditText = findViewById(R.id.sample_password);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.record360.ui.prefs", Context.MODE_PRIVATE);

        Button login = findViewById(R.id.sample_sign_in);
        login.setOnClickListener(v -> {
            if (usernameEditText != null && passwordEditText != null) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                if (username.length() > 0 && password.length() > 0) {
                    authenticateAndStart(this, username, password, this);
                } else {
                    start(this, this);
                }
            }
        });

        Button existingLogin = findViewById(R.id.existing_sign_in);
        final String userId = sharedPreferences.getString("USER_ID", null);
        final String token = sharedPreferences.getString("TOKEN", null);
        if (userId != null && token != null) {
            existingLogin.setEnabled(true);
            existingLogin.setOnClickListener(v -> {
                authenticatedStart(this, userId, token, null, this);
            });
        } else {
            existingLogin.setEnabled(false);
        }

        Button sdkLogin = findViewById(R.id.sdk_sign_in);
        sdkLogin.setOnClickListener(v -> {
            start(this, this);
        });
    }

    @Override
    public void onUserAuthenticated(String username, String userId, String token) {
        Log.i("MainActivity", "User " + username + " authenticated");

        SharedPreferences.Editor editor = this.getSharedPreferences("com.record360.ui.prefs", Context.MODE_PRIVATE).edit();
        editor.putString("USER_ID", userId);
        editor.putString("TOKEN", token);
        editor.apply();

        Button existingLogin = findViewById(R.id.existing_sign_in);
        if (userId != null && token != null) {
            existingLogin.setEnabled(true);
            existingLogin.setOnClickListener(v -> {
                authenticatedStart(this, userId, token, null, this);
            });
        }
    }

    @Override
    public void onFailedToAuthenticate(boolean credentialsAreValid, String error) {
        Log.e("MainActivity", "Authentication error: " + error);
    }

    @Override
    public Map<String, String> getTransactionData(String referenceNumber, Map<String, String> transactionData) {
        // Replace the first parameter with form and control names from your workflow you wish to replace. You can get a list of fields from the transactionData parameter
        transactionData.put("Inspection Report.Customer Name:", "John Doe");
        transactionData.put("Inspection Report.Multi Line Text Example:", referenceNumber);
        // Example for overwriting email_sent_to_list, accepts a list of emails (only valid emails will be added to transaction)
        // transactionData.put("email_sent_to_list", "john@domain.com, test@domain.org", "invalidWontBeAdded");
        return transactionData;
    }

    @Override
    public void onTransactionComplete(String referenceNumber) {
        Log.i("MainActivity", "Transaction " + referenceNumber + " complete");
    }

    @Override
    public void onTransactionCancelled(String referenceNumber) {
        Log.i("MainActivity", "Transaction " + referenceNumber + " cancelled");
    }

    @Override
    public void onTransactionUploadProgress(String referenceNumber, long complete, long total) {
        Log.i("MainActivity", "Transaction " + referenceNumber + " upload progress " + complete + "/" + total + " bytes");
    }

    @Override
    public void onTransactionUploaded(String referenceNumber) {
        Log.i("MainActivity", "Transaction " + referenceNumber + " uploaded");
    }

    @Override
    public void onTransactionUploadFailed(String referenceNumber, String error) {
        Log.i("MainActivity", "Transaction " + referenceNumber + " failed to upload with error: " + error);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop(this);
    }
}
