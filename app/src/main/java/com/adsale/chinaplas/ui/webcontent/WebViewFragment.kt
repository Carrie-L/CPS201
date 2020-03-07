package com.adsale.chinaplas.ui.webcontent


import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class WebViewFragment : Fragment() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(
            requireActivity(),
            MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)
    }

    private lateinit var webView: WebView
    //    private lateinit var progressBar: ProgressBar
    private var url: String = "https://www.chinaplasonline.com"
    private var title: String = "CPS20"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = view.findViewById(R.id.web_view)
//        progressBar = view.findViewById(R.id.progress_bar)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            url = WebViewFragmentArgs.fromBundle(it).url
            WebViewFragmentArgs.fromBundle(it).title?.let { label ->
                title = label
            }
        }
        mainViewModel.title.value = title

        LogUtil.i("webview url = $url")


        setProgressClient()
        setWebViewClient()
        webView.loadUrl(url)
    }

    private fun setProgressClient() {
        LogUtil.i("setProgressClient")

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                LogUtil.i("newProgress=$newProgress")
//                progressBar.progress = newProgress

//                if (newProgress >= 100) {
//                    progressBar.visibility = View.GONE
//                } else {
//                    if (progressBar.visibility == View.GONE) {
//                        progressBar.visibility = View.VISIBLE
//                    }
//                    progressBar.progress = newProgress
//                }
//                super.onProgressChanged(view, newProgress)
            }
        }
    }


    private fun setWebViewClient() {
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
//                progressBar.visibility=View.VISIBLE
            }
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                super.shouldOverrideUrlLoading(view, request)
                LogUtil.i("shouldOverrideUrlLoading= ${request?.url}")
                view?.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
//                progressBar.visibility=View.GONE
            }
        }
    }


}
