package com.magdv.dev.stagehostselector.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import android.support.v4.app.FragmentManager
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import com.magdv.dev.stagehostselector.Constants
import com.magdv.dev.stagehostselector.dialog.StageHostSelectorFragment

class StageHostSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTextColor(Color.WHITE)
        setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 138))
        setPadding(16, 16, 16, 16)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private var defaultHostUrl: String? = null

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private val preferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Constants.HOST_URL_STORAGE_KEY) {
                updateText()
            }
        }

    override fun onAttachedToWindow() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onDetachedFromWindow()
    }

    fun init(defaultHostUrl: String, fragmentManager: FragmentManager) {
        this.defaultHostUrl = defaultHostUrl
        updateText()

        setOnClickListener {
            val dialog = StageHostSelectorFragment.newInstance()
            dialog.show(fragmentManager, Constants.STAGE_HOST_SELECTOR_DIALOG_TAG)
        }
    }

    private fun updateText() {
        text = sharedPreferences.getString(Constants.HOST_URL_STORAGE_KEY, defaultHostUrl)
    }
}