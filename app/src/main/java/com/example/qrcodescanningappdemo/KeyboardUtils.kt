package com.example.qrcodescanningappdemo

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

fun hideSoftKeyboard(context: Context) {
    val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow((context as Activity).currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}