package com.example.frontend_moodify.presentation.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.frontend_moodify.R

class NameEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        // Tambahkan hint default
        hint = context.getString(R.string.name_hint)

        // Tambahkan listener untuk validasi saat teks berubah
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateName(s)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateName(input: CharSequence?) {
        if (!input.isNullOrEmpty()) {
            when {
                input.isBlank() -> {
                    error = context.getString(R.string.error_name_empty)
                }
                input.length < 3 -> {
                    error = context.getString(R.string.error_name_short)
                }
                else -> {
                    error = null // Tidak ada error
                }
            }
        }
    }
}
