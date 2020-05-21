package com.thegergo02.minkreta.ui

import android.content.Context
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thegergo02.minkreta.R

class UIHelper {
    companion object {
        fun generateWebView(ctx: Context, html: String, mimeType: String = "text/html", encoding: String = "UTF-8"): WebView {
            val webView = WebView(ctx)
            webView.loadData(html, mimeType, encoding)
            return webView
        }
        fun generateButton(ctx: Context, text: String,
                           clickListener: (v: View) -> List<View>? = {null}, showDetails: () -> Unit = {}, hideDetails: () -> Unit = {}, detailsLL: LinearLayout = LinearLayout(ctx),
                           textColor: Int = R.color.colorText,
                           backgroundColor: Int = R.color.colorPrimaryDark): Button {
            val button = Button(ctx)
            button.text = text
            button.setTextColor(ContextCompat.getColor(ctx, textColor))
            button.setBackgroundColor(ContextCompat.getColor(ctx, backgroundColor))
            button.setOnClickListener(wrapIntoDetails(clickListener, showDetails, hideDetails, detailsLL))
            return button
        }
        fun wrapIntoDetails(function: (v: View) -> List<View>?, showDetails: () -> Unit, hideDetails: () -> Unit, detailsLL: LinearLayout): (v: View) -> Unit {
            return {
                v: View ->
                hideDetails()
                val views = function(v)
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
        fun formatHtml(oldHtml: String): String {
            val cssString =
                "<style>body{background-color: black !important;color: white;}</style>"
            var newHtml = "${cssString}${oldHtml}"
            newHtml = newHtml.replace("style=\"color: black;\"", "style=\"color: white;\"")
            return newHtml.replace(
                "style=\"color: rgb(0, 0, 0);\"",
                "style=\"color: white;\""
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
