package com.example.frontend_moodify.presentation.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.frontend_moodify.R


class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        hint = context.getString(R.string.password_hint)
        inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty() && s.length < 8) {
                    error = context.getString(R.string.error_password_length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}