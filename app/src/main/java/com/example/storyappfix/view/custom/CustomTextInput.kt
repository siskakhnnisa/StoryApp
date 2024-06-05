package com.example.storyappfix.view.custom

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.example.storyappfix.R
import com.google.android.material.textfield.TextInputEditText


class CustomTextInput: TextInputEditText, TextWatcher {

    var isValid: Boolean? = null
        set(value) {
            field = value ?: (text?.isNotEmpty() == true)
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {
        addTextChangedListener(this)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        if (text != null && text?.isNotEmpty() == true) {
            if (inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                if (length() < 8) {
                    error = context.getString(R.string.password_less_8)
                    isValid = false
                } else {
                    isValid = true
                }
            } else if (inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
                    error = context.getString(R.string.invalid_email)
                    isValid = false
                } else {
                    isValid = true
                }
            } else {
                isValid = true
            }
        } else {
            error = context.getString(R.string.required_field)
            isValid = false
        }
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        error = null
    }
}