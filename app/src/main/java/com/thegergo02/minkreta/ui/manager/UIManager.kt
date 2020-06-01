package com.thegergo02.minkreta.ui.manager

import android.content.Context
import android.provider.Contacts
import android.view.View
import android.widget.*
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.activity.MainActivity
import com.thegergo02.minkreta.ui.SortType
import com.thegergo02.minkreta.ui.ThemeHelper
import com.thegergo02.minkreta.ui.UIHelper

open class UIManager (
    val ctx: Context,
    val themeHelper: ThemeHelper,
    val holder: LinearLayout,
    val button: Button,
    val getElemButtonColor: (RefreshableData) -> Int = {themeHelper.getColorFromAttributes(R.attr.colorButtonUnselected)},
    val getElemButtonTextColor: (RefreshableData) -> Int = {themeHelper.getColorFromAttributes(R.attr.colorText)},
    canClick: () -> Boolean,
    onEnterListener: () -> Unit,
    onExitListener: () -> Unit,
    val onElemClickListener: (View, RefreshableData) -> List<View>,
    val sortSpinner: Spinner? = null,
    var sortType: SortType? = null,
    spinnerElements: List<String>? = null,
    onItemSelectedListener: AdapterView.OnItemSelectedListener? = null
) {
    init {
        holder.visibility = View.GONE
        button.setOnClickListener {
            if (canClick()) {
                if (holder.visibility == View.GONE) {
                    onEnterListener()
                } else {
                    onExitListener()
                }
            }
        }
        if (sortSpinner != null && spinnerElements != null) {
            sortSpinner.onItemSelectedListener = onItemSelectedListener
            val spinnerLayouts = themeHelper.getResourcesFromAttributes(
                listOf(
                    R.attr.spinnerItemLayout,
                    R.attr.spinnerDropdownItemLayout
                )
            )
            val adapter =
                ArrayAdapter(ctx, spinnerLayouts[0], spinnerElements)
            adapter.setDropDownViewResource(spinnerLayouts[1])
            sortSpinner.adapter = adapter
        }
    }
    fun refresh(elems: List<RefreshableData>) {
        holder.removeAllViews()
        for (elem in elems) {
            val button = UIHelper.generateButton(ctx, elem.text, onElemClickListener, elem)
            button.setBackgroundColor(getElemButtonColor(elem))
            button.setTextColor(getElemButtonTextColor(elem))
            holder.addView(button)
        }
    }
    fun setVisibility(visibility: Int) {
        holder.visibility = visibility
        sortSpinner?.visibility = visibility
        button.setBackgroundColor(themeHelper.getColorFromAttributes(if (visibility == View.GONE) R.attr.colorButtonUnselected else R.attr.colorButtonSelected))
    }
}