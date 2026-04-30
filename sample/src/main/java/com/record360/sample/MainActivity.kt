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

    override fun onUserAuthenticated(username: String?, userId: String?, token: String?) {
        Timber.i("User $username authenticated")
        val editor = getSharedPreferences("com.record360.ui.prefs", MODE_PRIVATE).edit()
        editor.putString("USER_ID", userId)
        editor.putString("TOKEN", token)
        editor.apply()

        binding.existingSignIn.isEnabled = !userId.isNullOrEmpty() && !token.isNullOrEmpty()
        binding.existingSignIn.setOnClickListener {
            handleAuthenticatedStart(userId, token)
        }
    }

    override fun onFailedToAuthenticate(credentialsAreValid: Boolean, error: String?) {
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

    override fun onInspectionComplete(referenceNumber: String?) {
        Log.i("MainActivity","Inspection $referenceNumber complete")
    }

    override fun onInspectionCancelled(referenceNumber: String?) {
        Log.i("MainActivity","Inspection $referenceNumber cancelled")
    }

    override fun onInspectionUploadProgress(referenceNumber: String?, complete: Long, total: Long) {
        Log.i("MainActivity","Inspection $referenceNumber upload progress $complete/$total bytes")
    }

    override fun onInspectionUploaded(referenceNumber: String?, inspectionJson: String?) {
        Log.i("MainActivity","Inspection $referenceNumber uploaded")
    }

    override fun onInspectionUploadFailed(referenceNumber: String?, error: String?) {
        Log.i("MainActivity","Inspection $referenceNumber failed to upload with error: $error")
    }

    override fun onDestroy() {
        super.onDestroy()
        Record360SDK.r360Interface = null
    }
}
