package com.project.module2.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.module2.R
import com.project.module2.model.FormField

class FormAdapter(
    private val formFields: List<FormField>,
    private val listener: FormSubmitListener
) : RecyclerView.Adapter<FormAdapter.FormViewHolder>() {

    private val formData = mutableMapOf<FormField, String>()

    interface FormSubmitListener {
        fun onFormFieldChanged(field: FormField, value: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_form_field, parent, false)
        return FormViewHolder(view)
    }

    override fun onBindViewHolder(holder: FormViewHolder, position: Int) {
        val field = formFields[position]
        holder.bind(field)
    }

    override fun getItemCount() = formFields.size

    fun getFormData(): Map<FormField, String> = formData

    inner class FormViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val labelTextView: TextView = itemView.findViewById(R.id.labelTextView)
        private val inputEditText: TextInputEditText = itemView.findViewById(R.id.inputEditText)
        private val dropdownSpinner: Spinner = itemView.findViewById(R.id.dropdownSpinner)
        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val textInputLayout: TextInputLayout = itemView.findViewById(R.id.textInputLayout)
        private val requiredIndicator: TextView = itemView.findViewById(R.id.requiredIndicator)
        private var currentTextWatcher: TextWatcher? = null

        fun bind(field: FormField) {
            // Remove previous watcher to avoid recursion and issues with recycling
            currentTextWatcher?.let { inputEditText.removeTextChangedListener(it) }
            
            labelTextView.text = field.label
            requiredIndicator.visibility = if (field.isRequired) View.VISIBLE else View.GONE

            // Clear previous states
            inputEditText.visibility = View.GONE
            textInputLayout.visibility = View.GONE
            dropdownSpinner.visibility = View.GONE
            checkbox.visibility = View.GONE

            when (field.fieldType) {
                "text", "email", "number" -> {
                    textInputLayout.visibility = View.VISIBLE
                    inputEditText.visibility = View.VISIBLE
                    
                    // Set hint on Layout only, not the EditText itself to avoid "blur" / overlapping
                    textInputLayout.hint = field.hint ?: "Enter ${field.label}"
                    inputEditText.hint = null 

                    inputEditText.inputType = when (field.fieldType) {
                        "email" -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        "number" -> InputType.TYPE_CLASS_NUMBER
                        else -> InputType.TYPE_CLASS_TEXT
                    }

                    val currentValue = formData[field] ?: field.defaultValue ?: ""
                    inputEditText.setText(currentValue)

                    currentTextWatcher = object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                        override fun afterTextChanged(s: Editable?) {
                            val value = s.toString()
                            formData[field] = value
                            listener.onFormFieldChanged(field, value)
                        }
                    }
                    inputEditText.addTextChangedListener(currentTextWatcher)
                }

                "dropdown" -> {
                    dropdownSpinner.visibility = View.VISIBLE
                    val options = field.options ?: listOf("Select an option")
                    val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, options)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    dropdownSpinner.adapter = adapter

                    val savedValue = formData[field] ?: field.defaultValue
                    val position = options.indexOf(savedValue)
                    if (position >= 0) dropdownSpinner.setSelection(position)

                    dropdownSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                            val value = options[pos]
                            formData[field] = value
                            listener.onFormFieldChanged(field, value)
                        }
                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }

                "checkbox" -> {
                    checkbox.visibility = View.VISIBLE
                    checkbox.text = field.label
                    val isChecked = (formData[field] ?: field.defaultValue ?: "false").toBoolean()
                    checkbox.isChecked = isChecked
                    
                    checkbox.setOnCheckedChangeListener { _, checked ->
                        formData[field] = checked.toString()
                        listener.onFormFieldChanged(field, checked.toString())
                    }
                }
            }
        }
    }
}