package com.adsale.chinaplas.ui.webcontent


import android.text.TextUtils
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getWebContentHtmlPath
import com.adsale.chinaplas.viewmodels.WebContentViewModel

/**
 * A simple [Fragment] subclass.
 */
class WebContentFragment : BaseFragment() {
    private lateinit var webview: WebView
    private var pageID: String? = ""
    private lateinit var viewModel: WebContentViewModel

    override fun initedView(inflater: LayoutInflater) {
        val view = inflater.inflate(R.layout.fragment_web_content, baseFrame, true)
        webview = view.findViewById(R.id.web_content_view)
    }

    override fun initView() {
    }

    override fun initedData() {
    }

    override fun initData() {
        arguments?.let {
            WebContentFragmentArgs.fromBundle(it).pageID?.let { id ->
                pageID = id
            }
            WebContentFragmentArgs.fromBundle(it).title?.let { title ->
                mainViewModel.title.value = title
            }
        }

        viewModel = ViewModelProviders.of(this).get(WebContentViewModel::class.java)

//        webview.loadUrl(String.format(viewModel.htmlPath.value!!, pageID))
        webview.loadUrl(getWebContentHtmlPath(pageID!!))

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                LogUtil.i("url = $url")
                if (!TextUtils.isEmpty(url)) {
                    if (url == "next://Prereg-Yes") {
                        findNavController().navigate(R.id.myChinaplasLoginFragment)
                    } else if (url == "next://Prereg-No") {
                        findNavController().navigate(R.id.registerPreFragment)
                    }
                }
                return true
            }
        }
    }


    override fun back() {
    }
}
