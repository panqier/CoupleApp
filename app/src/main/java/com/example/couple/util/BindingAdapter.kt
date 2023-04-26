package com.example.couple.util

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("app:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.isVisible = visible
}