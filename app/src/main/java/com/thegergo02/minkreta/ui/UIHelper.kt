package com.thegergo02.minkreta.ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.R
import com.thegergo02.minkreta.ui.manager.RefreshableData

class UIHelper {
    companion object {
        fun generateWebView(ctx: Context, html: String, mimeType: String = "text/html", encoding: String = "UTF-8"): WebView {
            val webView = WebView(ctx)
            webView.loadData(html, mimeType, encoding)
            return webView
        }
        fun generateButton(ctx: Context, text: String,
                           clickListener: (View, RefreshableData) -> List<View>? = {_, _ -> null}, elem: RefreshableData? = null, showDetails: () -> Unit = {}, hideDetails: () -> Unit = {}, detailsLL: LinearLayout = LinearLayout(ctx),
                           style: Int? = null): Button {
            var button = Button(ctx)
            if (style != null) {
                button = Button(ctx, null, style)
            }
            button.text = text
            if (elem != null)
                button.setOnClickListener(wrapIntoDetails(clickListener, elem, showDetails, hideDetails, detailsLL))
            return button
        }
        fun wrapIntoDetails(function: (View, RefreshableData) -> List<View>?, elem: RefreshableData, showDetails: () -> Unit, hideDetails: () -> Unit, detailsLL: LinearLayout): (v: View) -> Unit {
            return {
                v: View ->
                hideDetails()
                val views = function(v, elem)
                if (views != null) {
                    for (view in views) {
                        detailsLL.addView(view)
                    }
                }
                showDetails()
            }
        }

        fun decodeHtml(escapedHtml: String): String {
            var newHtml = escapedHtml.replace("&lt;", "<")
            newHtml = newHtml.replace("&quot;", "\"")
            return newHtml.replace("&gt;", ">")
        }
        fun formatHtml(oldHtml: String, background: Int, text: Int): String {
            val backgroundColor = Color.valueOf(background)
            val backgroundColorText = "rgb(${backgroundColor.red() * 255}, ${backgroundColor.green() * 255}, ${backgroundColor.blue() * 255})"
            val textColor = Color.valueOf(text)
            val textColorText = "rgb(${textColor.red() * 255}, ${textColor.green() * 255}, ${textColor.blue() * 255})"
            val cssString =
                "<style>body{background-color: $backgroundColorText !important;color: $textColorText;}</style>"
            var newHtml = "${cssString}${oldHtml}"
            newHtml = newHtml.replace("style=\"color: black;\"", "style=\"color: $textColorText;\"")
            return newHtml.replace(
                "style=\"color: rgb(0, 0, 0);\"",
                "style=\"color: $textColorText;\""
            )
        }
        fun displayError(ctx: Context, layout: View, error: String) {
            val errorSnack = Snackbar.make(layout, error, Snackbar.LENGTH_LONG)
            errorSnack.view.setBackgroundColor(ContextCompat.getColor(ctx,
                R.color.colorError
            ))
            errorSnack.show()
        }
        fun displaySuccess(ctx: Context, layout: View, success: String) {
            val successSnack = Snackbar.make(layout, success, Snackbar.LENGTH_LONG)
            successSnack.view.setBackgroundColor(ContextCompat.getColor(ctx,
                R.color.colorSuccess
            ))
            successSnack.show()
        }
    }
}
