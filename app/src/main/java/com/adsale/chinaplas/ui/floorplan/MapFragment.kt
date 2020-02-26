package com.adsale.chinaplas.ui.floorplan


import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.palmap.h5calllibpalmap.JavaScriptCall
import cn.palmap.h5calllibpalmap.X5WebView
import com.adsale.chinaplas.R
import com.baidu.speech.utils.LogUtil
import com.tencent.smtt.sdk.WebView


/**
 * A simple [Fragment] subclass.
 */
open class MapFragment : Fragment() {
    private lateinit var webView: X5WebView
    private var webCall: JavaScriptCall? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        webView = view.findViewById(R.id.map_web_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        webView.loadUrl("https://expo.ipalmap.com/expo_TEM/index.html#/?name=CPS2020")
        webCall = JavaScriptCall(requireContext(), webView, 1)
        LogUtil.i(if (webView.x5WebViewExtension == null) "空的" else "不空")

        webView.webViewClient = object : com.tencent.smtt.sdk.WebViewClient() {

            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                super.onPageStarted(p0, p1, p2)

                LogUtil.i("onPageStarted:: p1=$p1")
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
                LogUtil.i("onPageFinished:: p1=$p1")
            }

            override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                LogUtil.i("shouldOverrideUrlLoading:: p1=$p1")
                return super.shouldOverrideUrlLoading(p0, p1)



            }

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        webCall?.destroy()
    }


}
