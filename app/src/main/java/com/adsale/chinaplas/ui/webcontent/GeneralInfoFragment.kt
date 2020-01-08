package com.adsale.chinaplas.ui.webcontent


import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.MenuAdapter
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.HtmlText
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.dao.WebContentRepository
import com.adsale.chinaplas.databinding.ItemMenuBinding
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.utils.getHtmName
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.tencent.bugly.Bugly
import kotlinx.coroutines.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class GeneralInfoFragment : Fragment() {
    //    private lateinit var binding:GeneralInfoFragment
//    private lateinit var webView: WebView
    private lateinit var rvHtml: RecyclerView
    private var baiduTJ: String? = "GeneralInformation"
    private var title: String? = ""
    private var path: String? = ""
    private var list = MutableLiveData<List<String>>()
    private lateinit var webContentRepository: WebContentRepository

    private var job = Job()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)

    val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity(), MainViewModelFactory(requireActivity().application,
            MainIconRepository.getInstance(CpsDatabase.getInstance(Bugly.applicationContext).mainIconDao()))
        )
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_general_info, container, false)
//        webView = view.findViewById(R.id.webview_general_info)
        rvHtml = view.findViewById(R.id.rv_general_info)

        arguments?.let {
            GeneralInfoFragmentArgs.fromBundle(it).baiduTJ?.let { parentID ->
                baiduTJ = parentID
            }

//            title = GeneralInfoFragmentArgs.fromBundle(it).title
//            title?.let {
//                mainViewModel.title.value = title
//            }
        }

        view.findViewById<TextView>(R.id.general_open).setOnClickListener {
            rvHtml.scrollToPosition(0)
        }
        view.findViewById<TextView>(R.id.general_scope).setOnClickListener {
            rvHtml.scrollToPosition(1)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        webContentRepository =
            WebContentRepository.getInstance(CpsDatabase.getInstance(context!!).webContentDao(),
                CpsDatabase.getInstance(context!!).htmlTextDao(),CpsDatabase.getInstance(context!!).fileControlDao())

        uiScope.launch {
            list.value = getPageIds()
        }

        list.observe(this, Observer {
            rvHtml.layoutManager = LinearLayoutManager(context!!, LinearLayoutManager.HORIZONTAL, false)
            rvHtml.setHasFixedSize(true)
            rvHtml.adapter = HorizontalAdapter(it)
        })


    }

    private suspend fun getPageIds(): List<String> {
        return withContext(Dispatchers.IO) {
            webContentRepository.getPageIDs(baiduTJ!!)
        }
    }

//    private fun loadLocalHtml(htmlName: String) {
//        val sb = StringBuilder()
//        if (File(rootDir.concat(mIntentUrl)).exists()) {
//            sb.append("file://").append(App.rootDir).append(mIntentUrl).append("/").append(htmlName)
//        } else {
//            sb.append("file:///android_asset/").append("WebContent/").append("/").append(htmlName)
//        }
//        webView.loadUrl(sb.toString())
//        i(TAG, "loadLocalHtml= $sb")
//        showD7()
//    }

    class HorizontalAdapter(val list: List<String>) :
        RecyclerView.Adapter<HorizontalAdapter.HorizontalViewHolder>() {

        class HorizontalViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

            companion object {
                fun from(parent: ViewGroup): HorizontalViewHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_html, parent, false)
//                    val frameLayout = view.findViewById<FrameLayout>(R.id.frame_html)
//                    frameLayout.removeView()
//                    frameLayout.addView(webView)
                    return HorizontalViewHolder(view)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
            return HorizontalViewHolder.from(parent)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
            val sdPath = "file://${rootDir}WebContent/${list[position]}/${getHtmName()}"
            if (File(sdPath).exists()) {
                i("sdPath=$sdPath")
                holder.itemView.findViewById<WebView>(R.id.wv_html).loadUrl(sdPath)
            } else {
                val assetPath = "file:///android_asset/WebContent/${list[position]}/${getHtmName()}"
                i("assetPath=$assetPath")
                holder.itemView.findViewById<WebView>(R.id.wv_html).loadUrl(assetPath)
            }
        }
    }


}
