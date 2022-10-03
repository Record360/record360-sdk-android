package com.record360.sample

import com.record360.sdk.Record360Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.record360.sample.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : Record360Activity(), Record360Activity.Record360Interface {
    private lateinit var binding: ActivityMainBinding
    private var intentMode = IntentMode.Unit
    private enum class IntentMode {
        Unit,
        Task,
        WorkOrder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SampleMultiDexApplication.getApplicationComponent().inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val sharedPreferences = getSharedPreferences("com.record360.ui.prefs", MODE_PRIVATE)
        binding.sampleSignIn.setOnClickListener {
            val username = binding.sampleUsername.text.toString()
            val password = binding.samplePassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                authenticateAndStart(this, username, password, getUnitId(), this)
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
                startWithTaskId(this, binding.taskId.text.toString(), this)
            } else {
                startWithLogin(this, getUnitId(), null, getWOId(), getWOLabel(), this)
            }
        }
        binding.possibleIntentsGroup.setOnCheckedChangeListener { _, checkedId ->
            intentMode = when (checkedId) {
                R.id.unit_intent_button -> IntentMode.Unit
                R.id.task_intent_button -> IntentMode.Task
                else -> IntentMode.WorkOrder
            }
            binding.unitId.visibility = if (checkedId == R.id.unit_intent_button || checkedId == R.id.wo_intent_button)
                View.VISIBLE else View.GONE
            binding.taskId.visibility = if (checkedId == R.id.task_intent_button) View.VISIBLE else View.GONE
            binding.woDetailsLayout.visibility = if (checkedId == R.id.wo_intent_button) View.VISIBLE else View.GONE
        }
    }

    private fun getUnitId(): String? {
        val text = binding.unitId.text.toString()
        return if (intentMode == IntentMode.Unit && text.isNotEmpty()) text else null
    }

    private fun getWOId(): Int? {
        val id = binding.woId.text.toString().toIntOrNull()
        return if (intentMode == IntentMode.WorkOrder && id != null) id else null
    }

    private fun getWOLabel(): String? {
        val label = binding.woLabel.text.toString()
        return if (intentMode == IntentMode.WorkOrder && label.isNotEmpty()) label else null
    }

    private fun handleAuthenticatedStart(userId: String?, token: String?) {
        if (intentMode == IntentMode.Task) {
            startWithTaskId(this, binding.taskId.text.toString(), this)
        } else {
            authenticatedStart(
                this,
                userId,
                token!!,
                getUnitId(),
                getWOId(),
                getWOLabel(),
                this
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
    ): Map<String?, String?>? {
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

    override fun onInspectionUploaded(referenceNumber: String?) {
        Log.i("MainActivity","Inspection $referenceNumber uploaded")
    }

    override fun onInspectionUploadFailed(referenceNumber: String?, error: String?) {
        Log.i("MainActivity","Inspection $referenceNumber failed to upload with error: $error")
    }

    override fun onDestroy() {
        super.onDestroy()
        stop(this)
    }
}
