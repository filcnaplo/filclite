package hu.filcnaplo.ellenorzo.lite.ui.manager

import android.content.Context
import android.view.View
import android.widget.*
import hu.filcnaplo.ellenorzo.lite.R
import hu.filcnaplo.ellenorzo.lite.ui.SortType
import hu.filcnaplo.ellenorzo.lite.ui.ThemeHelper
import hu.filcnaplo.ellenorzo.lite.ui.UIHelper

open class UIManager (
    val ctx: Context,
    val themeHelper: ThemeHelper,
    val holder: LinearLayout,
    val button: Button,
    val getElemButtonColor: (RefreshableData) -> Int,
    val getElemButtonTextColor: (RefreshableData) -> Int,
    canClick: () -> Boolean,
    onEnterListener: () -> Unit,
    onExitListener: () -> Unit,
    val onRefreshListener: () -> Unit?,
    val onElemClickListener: (View, RefreshableData) -> List<View>,
    val toggleDetails: (Boolean) -> Unit,
    val detailsLL: LinearLayout,
    val sortSpinner: Spinner? = null,
    var sortType: SortType? = null,
    spinnerElements: List<String>? = null,
    onItemSelectedListener: AdapterView.OnItemSelectedListener? = null,
    val customViews: List<View>? = null
) {
    var firstSpinnerSelection = true
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
            setSpinnerElements(spinnerElements)
        }
    }
    fun refresh(elems: List<RefreshableData>) {
        holder.removeAllViews()
        onRefreshListener()
        for (elem in elems) {
            val button = UIHelper.generateButton(ctx, elem.text, onElemClickListener, elem, toggleDetails, detailsLL)
            button.setBackgroundColor(getElemButtonColor(elem))
            button.setTextColor(getElemButtonTextColor(elem))
            holder.addView(button)
        }
    }
    fun setVisibility(visibility: Int) {
        holder.visibility = visibility
        sortSpinner?.visibility = visibility
        if (customViews != null) {
            for (view in customViews) {
                view.visibility = visibility
            }
        }
        button.setBackgroundColor(themeHelper.getColorFromAttributes(if (visibility == View.GONE) R.attr.colorButtonUnselected else R.attr.colorButtonSelected))
    }

    fun setSpinnerElements(elems: List<String>) {
        val spinnerLayouts = themeHelper.getResourcesFromAttributes(
            listOf(
                R.attr.sortSpinnerItemLayout,
                R.attr.sortSpinnerDropdownItemLayout
            )
        )
        val adapter =
            ArrayAdapter(ctx, spinnerLayouts[0], elems)
        adapter.setDropDownViewResource(spinnerLayouts[1])
        sortSpinner?.adapter = adapter
    }
}