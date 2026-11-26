package com.abrebo.tabletennishub.utils

import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.abrebo.tabletennishub.R

/** Keeps screen content clear of the system status and navigation bars on edge-to-edge devices. */
fun ComponentActivity.configureSystemBarInsets(content: View) {
    enableEdgeToEdge()
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

    val contentFrame = findViewById<FrameLayout>(android.R.id.content)
    val statusBarBackground = View(this).apply {
        setBackgroundColor(ContextCompat.getColor(this@configureSystemBarInsets, R.color.main_color))
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        elevation = 100f
    }
    contentFrame.addView(
        statusBarBackground,
        FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, Gravity.TOP)
    )

    ViewCompat.setOnApplyWindowInsetsListener(content) { view, windowInsets ->
        val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        statusBarBackground.layoutParams = statusBarBackground.layoutParams.apply {
            height = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        }
        windowInsets
    }
    ViewCompat.requestApplyInsets(content)
}
