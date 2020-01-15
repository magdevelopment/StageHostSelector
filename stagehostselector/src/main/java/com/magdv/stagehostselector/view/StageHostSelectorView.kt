package com.magdv.stagehostselector.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.magdv.stagehostselector.Constants
import com.magdv.stagehostselector.dialog.StageHostSelectorDialogFragment
import com.magdv.stagehostselector.repository.StageHostSelectorRepository

class StageHostSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val repository: StageHostSelectorRepository? by lazy {
        StageHostSelectorRepository.getInstance()
    }

    private val fragmentManager: FragmentManager?
        get() = (context as? FragmentActivity)?.supportFragmentManager

    private val sharedPreferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private val preferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Constants.HOST_URL_STORAGE_KEY) {
                updateText()
            }
        }

    init {
        updateText()
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTextColor(Color.WHITE)
        setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 138))
        setPadding(16, 16, 16, 16)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setOnClickListener {
            val dialog = StageHostSelectorDialogFragment()
            dialog.show(fragmentManager, Constants.STAGE_HOST_SELECTOR_DIALOG_TAG)
        }
    }

    private fun updateText() {
        text = repository?.getCurrentHostUrl() ?: repository?.getDefaultHostUrl()
    }

    override fun onAttachedToWindow() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onDetachedFromWindow()
    }
}