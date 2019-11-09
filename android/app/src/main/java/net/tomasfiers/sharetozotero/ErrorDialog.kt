package net.tomasfiers.sharetozotero

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ErrorDialog(private val message: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
        builder.setNeutralButton("Shucks", DialogInterface.OnClickListener { dialog, int -> })
        return builder.create()
    }
}