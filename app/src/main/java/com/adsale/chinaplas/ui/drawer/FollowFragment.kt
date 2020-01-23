package com.adsale.chinaplas.ui.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.R
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.getHtmName
import com.adsale.chinaplas.viewmodels.FollowViewModel
import java.io.File

class FollowFragment : Fragment() {
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.layout_web_view, container, false)
        webView = root.findViewById(R.id.web_view)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sdPath = "file://${rootDir}WebContent/%s/${getHtmName()}"
        val assetPath = "file:///android_asset/WebContent/%s/${getHtmName()}"
        val path:String
        if (File(sdPath).exists()) {
            path = sdPath
        } else {
            path = assetPath
        }
        webView.loadUrl(String.format(path, "S005"))

    }
}