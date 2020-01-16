package com.magdv.stagehostselector.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.magdv.stagehostselector.Constants
import com.magdv.stagehostselector.common.CurrentHostUrlChangeListener
import com.magdv.stagehostselector.dialog.StageHostSelectorDialogFragment
import com.magdv.stagehostselector.repository.StageHostSelectorRepository

internal class StageHostSelectorView @JvmOverloads constructor(
    private val repository: StageHostSelectorRepository,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr),
    CurrentHostUrlChangeListener {


    private val fragmentManager: FragmentManager?
        get() = (context as? FragmentActivity)?.supportFragmentManager

    init {
        repository.subscribeCurrentHostUrl(this)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        setTextColor(Color.WHITE)
        setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 138))
        setPadding(16, 16, 16, 16)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setOnClickListener {
            val dialog = StageHostSelectorDialogFragment.newInstance(repository)
            dialog.show(fragmentManager, Constants.STAGE_HOST_SELECTOR_DIALOG_TAG)
        }
    }

    override fun onValueChanged(newCurrentHostUrl: String?) {
        text = newCurrentHostUrl ?: repository.getDefaultHostUrl()
    }
}