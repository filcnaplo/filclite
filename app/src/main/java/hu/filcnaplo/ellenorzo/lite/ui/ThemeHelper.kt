package hu.filcnaplo.ellenorzo.lite.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.TypedValue
import hu.filcnaplo.ellenorzo.lite.R

class ThemeHelper(val ctx: Context) {
    var sharedPreferences: SharedPreferences = ctx.getSharedPreferences(ctx.getString(R.string.theme_path), Context.MODE_PRIVATE)
    private fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean("dark", true)
    }
    fun setCurrentTheme() {
        ctx.setTheme(if (isDarkTheme()) R.style.DarkTheme else R.style.LightTheme)
    }
    fun getColorFromAttributes(attr: Int): Int {
        val typedValue = TypedValue()
        ctx.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }
    fun getResourcesFromAttributes(attrs: List<Int>): List<Int> {
        val intArray = IntArray(attrs.size)
        var count = -1
        for (attr in attrs) {
            intArray[++count] = attr
        }
        val ta = ctx.obtainStyledAttributes(intArray)
        count = -1
        val resources = mutableListOf<Int>()
        for (res in attrs) {
            resources.add(ta.getResourceId(++count, 0))
        }
        ta.recycle()
        return resources
    }
}