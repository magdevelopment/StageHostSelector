package com.magdv.stagehostselector.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.magdv.stagehostselector.R
import com.magdv.stagehostselector.repository.StageHostSelectorRepository
import kotlinx.android.synthetic.main.shs_dialog_stage_host_selector.*

internal class StageHostSelectorDialogFragment : BottomSheetDialogFragment() {

    private var suggestionHostUrls: MutableSet<String> = mutableSetOf()
    private var hostUrls: List<String> = emptyList()
    private var currentHostUrl: String? = null
    private val repository: StageHostSelectorRepository by lazy {
        StageHostSelectorRepository.getInstance(PreferenceManager.getDefaultSharedPreferences(context))
    }

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

    private fun validateUrl(url: String): Boolean {
        return when {
            url.isBlank() -> {
                textInputLayout.error = getString(R.string.shs_error_empty_url_input)
                return false
            }

            else -> true
        }
    }

    private fun loadSuggestions() {
        suggestionHostUrls = repository.getSuggestionHostUrls().toMutableSet()
        currentHostUrl = repository.getCurrentHostUrl()
    }

    private fun onRemoveHostUrl(id: Int) {
        val urlToRemove = hostUrls[id]

        if (urlToRemove == currentHostUrl) {
            onHostUrlSelected(null)
        }

        suggestionHostUrls.remove(urlToRemove)
        saveSuggestions()
        showSuggestions()
    }

    private fun onHostUrlSelected(url: String?) {
        currentHostUrl = url

        repository.setCurrentHostUrl(url)
    }

    private fun saveSuggestions() {
        repository.setSuggestionHostUrls(suggestionHostUrls)
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
                setOnLongClickListener {
                    onLongClick(url)
                    true
                }
            }
            .also { chipGroup.addView(it) }
    }

    private fun onLongClick(url: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Url", url)
        clipboard.primaryClip = clip

        Toast.makeText(requireContext(), R.string.shs_copied_to_clipboard, Toast.LENGTH_SHORT)
            .show()
    }
}
