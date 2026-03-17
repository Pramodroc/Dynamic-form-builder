package com.project.module2.model // Changed package

import com.google.gson.annotations.SerializedName

data class FormField(
    @SerializedName("fieldId")
    val fieldId: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("fieldType")
    val fieldType: String, // "text", "email", "number", "dropdown", "checkbox", "date"

    @SerializedName("hint")
    val hint: String? = null,

    @SerializedName("isRequired")
    val isRequired: Boolean = false,

    @SerializedName("validationRules")
    val validationRules: ValidationRules? = null,

    @SerializedName("options")
    val options: List<String>? = null, // For dropdown fields

    @SerializedName("defaultValue")
    val defaultValue: String? = null
)

data class ValidationRules(
    @SerializedName("minLength")
    val minLength: Int? = null,

    @SerializedName("maxLength")
    val maxLength: Int? = null,

    @SerializedName("pattern")
    val pattern: String? = null,

    @SerializedName("minValue")
    val minValue: Int? = null,

    @SerializedName("maxValue")
    val maxValue: Int? = null
)