package com.magdv.stagehostselector.dialog

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.magdv.stagehostselector.Constants
import com.magdv.stagehostselector.R
import kotlinx.android.synthetic.main.shs_dialog_stage_host_selector.*

internal class StageHostSelectorDialogFragment : BottomSheetDialogFragment() {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private var suggestionHostUrls: MutableSet<String> = mutableSetOf()
    private var hostUrls: List<String> = emptyList()
    private var currentHostUrl: String? = null

    override fun getTheme(): Int {
        return R.style.SHS_BottomSheetDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.shs_dialog_stage_host_selector, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        loadSuggestions()
        showSuggestions()
    }

    private fun initViews() {
        urlEditText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    urlEditText.text.toString()
                        .takeIf { validateUrl(it) }
                        ?.let { url ->
                            urlEditText.text?.clear()
                            suggestionHostUrls.add(url)
                            saveSuggestions()
                            showSuggestions()
                        } ?: return@setOnEditorActionListener true

                    false
                }

                else -> false
            }
        }

        urlEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }
        })

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedUrl = if (checkedId < 0) null else hostUrls[checkedId]
            if (selectedUrl == null || selectedUrl != currentHostUrl) {
                onHostUrlSelected(selectedUrl)
            }
        }
    }

    private fun onHostUrlSelected(url: String?) {
        currentHostUrl = url

        preferences.edit()
            .putString(Constants.HOST_URL_STORAGE_KEY, url)
            .apply()
    }

    private fun validateUrl(url: String): Boolean {
        return when {
            url.isBlank() -> {
                textInputLayout.error = getString(R.string.shs_error_empty_url_input)
                return false
            }

            else -> true
        }
    }

    private fun saveSuggestions() {
        preferences.edit()
            .putStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, suggestionHostUrls)
            .apply()
    }

    private fun loadSuggestions() {
        suggestionHostUrls = preferences
            .getStringSet(Constants.HOST_URL_SUGGESTIONS_STORAGE_KEY, null)
            ?.toMutableSet() ?: mutableSetOf()

        currentHostUrl = preferences
            .getString(Constants.HOST_URL_STORAGE_KEY, null)
    }

    private fun showSuggestions() {
        chipGroup.removeAllViews()

        hostUrls = suggestionHostUrls.sortedBy { it }
        hostUrls.forEachIndexed { index, url ->
            addSuggestedUrl(index, url, url == currentHostUrl)
        }
    }

    private fun addSuggestedUrl(id: Int, url: String, isChecked: Boolean) {
        Chip(chipGroup.context)
            .apply {
                isCheckable = true
                isCloseIconVisible = true
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Caption)

                this.id = id
                text = url
                this.isChecked = isChecked

                ellipsize = TextUtils.TruncateAt.MIDDLE

                setOnCloseIconClickListener { onRemoveHostUrl(it.id) }
            }
            .also { chipGroup.addView(it) }
    }

    private fun onRemoveHostUrl(id: Int) {
        val urlToRemove = hostUrls[id]

        if(urlToRemove == currentHostUrl) {
            onHostUrlSelected(null)
        }

        suggestionHostUrls.remove(urlToRemove)
        saveSuggestions()
        showSuggestions()
    }
}
