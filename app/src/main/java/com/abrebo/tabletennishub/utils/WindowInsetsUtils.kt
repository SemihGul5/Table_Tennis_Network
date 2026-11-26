package com.abrebo.tabletennishub.utils

import android.app.Activity
import android.graphics.Color
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
fun Activity.configureSystemBarInsets(content: View) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = Color.TRANSPARENT
    window.navigationBarColor = Color.TRANSPARENT
    ViewCompat.getWindowInsetsController(content)?.isAppearanceLightStatusBars = false

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
