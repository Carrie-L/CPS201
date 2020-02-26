package com.adsale.chinaplas.ui.events


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.getHtmName
import com.adsale.chinaplas.utils.getItemEventID
import java.io.File

/**
 * setItemEventID()
 */
class EventDetailFragment : Fragment() {
    private lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_detail, container, false)
        webView = view.findViewById(R.id.event_web_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val eventID = getItemEventID()
        webView.loadUrl(String.format(getHtmlPath(), "PlasticsRecycling"))
    }

    private fun getHtmlPath(): String {
        val sdPath = "file://${rootDir}ConcurrentEvent/%s/${getHtmName()}"
        val assetPath = "file:///android_asset/ConcurrentEvent/%s/${getHtmName()}"
        return if (File(sdPath).exists()) {
            sdPath
        } else {
            assetPath
        }
    }


}
