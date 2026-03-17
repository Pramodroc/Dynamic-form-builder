package com.project.module2

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.project.module2.adapter.FormAdapter
import com.project.module2.model.FormField

class MainActivity : AppCompatActivity(), FormAdapter.FormSubmitListener {

    private lateinit var formAdapter: FormAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var submitButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var resultCard: View
    private val formFields = mutableListOf<FormField>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.formRecyclerView)
        submitButton = findViewById(R.id.submitButton)
        resultTextView = findViewById(R.id.resultTextView)
        resultCard = findViewById(R.id.resultCard)

        setupRecyclerView()
        loadFormConfiguration()
        setupSubmitButton()
    }

    private fun setupRecyclerView() {
        formAdapter = FormAdapter(formFields, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = formAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun loadFormConfiguration() {
        try {
            val jsonString = assets.open("form_config.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<FormField>>() {}.type
            val fields = Gson().fromJson<List<FormField>>(jsonString, type)

            if (fields != null) {
                formFields.clear()
                formFields.addAll(fields)
                formAdapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error: ${e.message}")
            Toast.makeText(this, "Failed to load form", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSubmitButton() {
        submitButton.setOnClickListener {
            validateAndSubmitForm()
        }
    }

    private fun validateAndSubmitForm() {
        val formData = formAdapter.getFormData()
        
        for (field in formFields) {
            val value = formData[field] ?: ""
            if (field.isRequired && value.isBlank()) {
                Toast.makeText(this, "${field.label} is required", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val displayMap = formData.map { it.key.label to it.value }.toMap()
        val prettyJson = Gson().newBuilder().setPrettyPrinting().create().toJson(displayMap)
        
        resultTextView.text = prettyJson
        resultCard.visibility = View.VISIBLE
        
        Toast.makeText(this, "Form submitted!", Toast.LENGTH_SHORT).show()
    }

    override fun onFormFieldChanged(field: FormField, value: String) {
        // Handle individual field changes
    }
}