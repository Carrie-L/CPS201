package com.adsale.chinaplas.ui.dialog

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.adsale.chinaplas.R

/**
 * Created by Carrie on 2019/12/9.
 */
class ProgressDialog : DialogFragment() {
    private lateinit var textView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_progress, container, false)
        textView = view.findViewById(R.id.dialog_msg)
        isCancelable = false
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun setMessage(msg: String) {
        textView.text = msg
    }

    fun isShowing(): Boolean {
        return isShowing()
    }


}