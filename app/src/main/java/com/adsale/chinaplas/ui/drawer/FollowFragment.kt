package com.adsale.chinaplas.ui.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.utils.getWebContentHtmlPath

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

        val path = getWebContentHtmlPath("S005")
        webView.loadUrl(String.format(path, "S005"))

//        val sdPath = "file://${rootDir}WebContent1/%s/${getHtmName()}"
//        val assetPath = "file:///android_asset/WebContent/%s/${getHtmName()}"
//        val path:String
//        if (File(sdPath).exists()) {
//            path = sdPath
//            LogUtil.i("sdPath exists = $sdPath")
//        } else {
//            path = assetPath
//            LogUtil.i("assetPath exists = $sdPath")
//        }


    }
}