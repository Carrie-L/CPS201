package com.adsale.chinaplas.ui.webcontent


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
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
    private lateinit var progressBar: ProgressBar
    private var url: String = "https://www.chinaplasonline.com"
    private var title: String = "CPS20"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = view.findViewById(R.id.web_view)
        progressBar = view.findViewById(R.id.progress_bar)
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

        webView.loadUrl(url)
        setProgressClient()
    }

    private fun setProgressClient() {
        LogUtil.i("setProgressClient")
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                LogUtil.i("newProgress=$newProgress")
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                } else {
                    if (progressBar.visibility == View.GONE) {
                        progressBar.visibility = View.VISIBLE
                    }
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }


}
