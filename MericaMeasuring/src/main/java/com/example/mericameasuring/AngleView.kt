package com.example.mericameasuring

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

class AngleView(
    private val rootView: View,
    private val statusTextView: TextView,
    private val horizontalAngleTextView: TextView,
    private val verticalAngleTextView: TextView,
    private val levelIndicator: ImageView,
    private val buttons: List<Button>
) {
    fun setStatusText(text: String) {
        statusTextView.text = text
    }

    fun setHorizontalAngleText(text: String) {
        horizontalAngleTextView.text = text
    }

    fun setVerticalAngleText(text: String) {
        verticalAngleTextView.text = text
    }

    fun setLevelIndicatorColor(colorResId: Int) {
        levelIndicator.setBackgroundResource(colorResId)
    }

    fun setSelectedButton(selectedButton: Button) {
        buttons.forEach { it.isEnabled = true }
        selectedButton.isEnabled = false
    }

    fun setBackgroundColor(colorResId: Int) {
        rootView.setBackgroundColor(ContextCompat.getColor(rootView.context, colorResId))
    }
}
