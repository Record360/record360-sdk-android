package com.record360.sample

import com.record360.sdk.Record360Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.record360.R
import com.record360.databinding.ActivityMainBinding
import com.record360.sdk.IRecord360Interface
import com.record360.sdk.R360Identity
import com.record360.sdk.Record360SDK
import timber.log.Timber

class MainActivity : Record360Activity(), IRecord360Interface {
    private lateinit var binding: ActivityMainBinding
    private var intentMode = IntentMode.Inspection
    private enum class IntentMode {
        Inspection,
        Task
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Record360SDK.r360Interface = this
        val sharedPreferences = getSharedPreferences("com.record360.ui.prefs", MODE_PRIVATE)
        binding.sampleSignIn.setOnClickListener {
            val username = binding.sampleUsername.text.toString()
            val password = binding.samplePassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                val identity = R360Identity.PasswordIdentity(username, password)
                startRecord360WithIdentity(this, identity, getUnitId(), getDepartment(), getSequence())
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_LONG).show()
            }
        }
        
        val userId = sharedPreferences.getString("USER_ID", null)
        val token = sharedPreferences.getString("TOKEN", null)
        binding.existingSignIn.isEnabled = userId != null && token != null
        binding.existingSignIn.setOnClickListener {
            handleAuthenticatedStart(userId, token)
        }
        binding.sdkSignIn.setOnClickListener {
            if (intentMode == IntentMode.Task) {
                startWithTaskId(this, binding.taskId.text.toString())
            } else {
                startRecord360(this, getUnitId(), getDepartment(), getSequence(), null)
            }
        }
        binding.possibleIntentsGroup.setOnCheckedChangeListener { _, checkedId ->
            intentMode = when (checkedId) {
                R.id.task_intent_button -> IntentMode.Task
                else -> IntentMode.Inspection
            }
            binding.taskId.visibility = if (checkedId == R.id.task_intent_button) View.VISIBLE else View.GONE
            binding.inspectionDetailsLayout.visibility = if (checkedId == R.id.inspection_intent_button) View.VISIBLE else View.GONE
        }
    }

    private fun getUnitId(): String? {
        val text = binding.unitId.text.toString()
        return if (intentMode == IntentMode.Inspection && text.isNotEmpty()) text else null
    }

    private fun getDepartment(): String? {
        val department = binding.department.text.toString()
        Timber.i("Department $department")
        return if (intentMode == IntentMode.Inspection && department.isNotEmpty()) department else null
    }

    private fun getSequence(): String? {
        val sequence = binding.sequence.text.toString()
        return if (intentMode == IntentMode.Inspection && sequence.isNotEmpty()) sequence else null
    }

    private fun handleAuthenticatedStart(userId: String?, token: String?) {
        if (intentMode == IntentMode.Task) {
            startWithTaskId(this, binding.taskId.text.toString())
        } else if (token != null) {
            val identity = R360Identity.UserTokenIdentity(userId, token)
            startRecord360WithIdentity(
                this,
                identity,
                getUnitId(),
                getDepartment(),
                getSequence()
            )
        }
    }

    override fun onSuccessfulAuthentication(userToken: String, userId: String) {
        Log.i("MainActivity","User $userId authenticated")
        val editor = getSharedPreferences("com.record360.ui.prefs", MODE_PRIVATE).edit()
        editor.putString("USER_ID", userId)
        editor.putString("TOKEN", userToken)
        editor.apply()

        binding.existingSignIn.isEnabled = userId.isNotEmpty() && userToken.isNotEmpty()
        binding.existingSignIn.setOnClickListener {
            handleAuthenticatedStart(userId, userToken)
        }
    }

    override fun onFailedAuthentication(error: Throwable) {
        Log.e("MainActivity", "Authentication error: $error")
    }

    override fun onReferenceNumberEnteredWithFieldData(
        referenceNumber: String?,
        fieldData: Map<String?, String?>?
    ): Map<String?, String?> {
        val map = fieldData?.toMutableMap() ?: mutableMapOf()
        map["Inspection Report.Customer Name:"] = "John Doe"
        map["Inspection Report.Multi Line Text Example:"] = referenceNumber
        // Example for overwriting email_sent_to_list, accepts a list of emails (only valid emails will be added to inspection)
        // inspectionData.put("email_sent_to_list", "john@domain.com, test@domain.org", "invalidWontBeAdded");

        return map
    }

    override fun onContractFieldData(contractFieldData: Map<String?, String?>?): Map<String?, String?>? {
        return contractFieldData
    }

    override fun onInspectionComplete(refNum: String, inspectionNonce: String) {
        Log.i("MainActivity","Inspection $refNum complete ($inspectionNonce)")
    }

    override fun onInspectionCancelled(refNum: String, inspectionNonce: String) {
        Log.i("MainActivity","Inspection $refNum cancelled ($inspectionNonce)")
    }

    override fun onInspectionUploadProgress(
        refNum: String,
        inspectionNonce: String,
        percentComplete: Double
    ) {
        Log.i("MainActivity","Inspection $refNum upload progress $percentComplete%")
    }

    override fun onInspectionUploaded(
        refNum: String,
        inspectionNonce: String,
        inspectionJSON: String
    ) {
        Log.i("MainActivity","Inspection $refNum uploaded ($inspectionNonce)")
    }

    override fun onInspectionUploadFailed(refNum: String, inspectionNonce: String, error: String) {
        Log.i("MainActivity","Inspection $refNum ($inspectionNonce) failed to upload with error: $error")
    }

    override fun onInspectionStarted(refNum: String, inspectionNonce: String) {
        Log.i("MainActivity","Inspection $refNum started ($inspectionNonce)")
    }

    override fun onInspectionUploadDeleted(refNum: String, inspectionNonce: String) {
        Log.i("MainActivity","Inspection $refNum deleted from uploads ($inspectionNonce)")
    }

    override fun onSDKShouldExit() {
        Log.i("MainActivity","Exited Record360 SDK")
    }

    override fun onSDKShouldRelaunch() {
        Log.i("MainActivity","SDK requesting relaunch")
    }

    override fun onDestroy() {
        super.onDestroy()
        Record360SDK.r360Interface = null
    }
}
