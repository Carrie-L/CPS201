package com.adsale.chinaplas.ui.webcontent


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.lifecycle.ViewModelProviders

import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.getHtmName
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
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

        webview.loadUrl(String.format(viewModel.htmlPath.value!!, pageID))
    }

    override fun back() {
    }
}
