package aosp.toolkit.perseus.fragments.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.widget.TextView

import aosp.toolkit.perseus.R

/*
 * @File:   LicenceDialogFragment
 * @Author: 1552980358
 * @Time:
 * @Date:   31 Mar 2019
 *
 */

class LicenceDialogFragment : DialogFragment() {
    @SuppressLint("InflateParams")
    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialogfragment_licence, null)
        //val webView = view.findViewById<WebView>(R.id.webView)
        val textView = view.findViewById<TextView>(R.id.textView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (i in context!!.assets.open("license.txt").bufferedReader(Charsets.UTF_8).readLines()) {
                textView.append(Html.fromHtml(i, FROM_HTML_MODE_LEGACY))
                textView.append("\n")
            }
        } else {
            for (i in context!!.assets.open("credit.txt").bufferedReader(Charsets.UTF_8).readLines()) {
                textView.append(Html.fromHtml(i))
                textView.append("\n")
            }
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        builder.setView(view).setTitle(R.string.licence)
        builder.setPositiveButton(R.string.yes) { _, _ -> }
        return builder.create()
    }
}