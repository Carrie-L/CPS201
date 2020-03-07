package com.adsale.chinaplas.ui.webcontent


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.adapters.TipAdapter
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.WebContent
import com.adsale.chinaplas.data.dao.WebContentRepository
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.helper.D5_VISIT
import com.adsale.chinaplas.utils.getSpanCount
import com.adsale.chinaplas.utils.setItemEventID
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
class VisitorTipFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var baiduTJ: String? = "Opening"
    private var job = Job()
    private var list = MutableLiveData<List<WebContent>>()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)
    private lateinit var webContentRepository: WebContentRepository
    private lateinit var ivD5: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_visitor_tip, container, false)
        recyclerView = view.findViewById(R.id.rv_tips)
        ivD5 = view.findViewById(R.id.iv_d5)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        webContentRepository =
            WebContentRepository.getInstance(CpsDatabase.getInstance(context!!).webContentDao(),
                CpsDatabase.getInstance(context!!).htmlTextDao(),CpsDatabase.getInstance(context!!).fileControlDao())

        arguments?.let {
            VisitorTipFragmentArgs.fromBundle(it).baiduTJ?.let { parentID ->
                baiduTJ = parentID
            }
        }

        uiScope.launch {
            list.value = getWebContents()
        }

        list.observe(this, Observer {
            val layoutManager = GridLayoutManager(context, getSpanCount())
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            val adapter = TipAdapter(it, OnItemClickListener { item, position ->
                item as WebContent
                this.findNavController()
                    .navigate(VisitorTipFragmentDirections.actionVisitorTipFragmentToWebContentFragment(item.PageID,
                        item.getTitle()))
            })
            recyclerView.adapter = adapter
        })

        showD5()

    }

    private suspend fun getWebContents(): List<WebContent> {
        return withContext(Dispatchers.IO) {
            webContentRepository.getWebContents(baiduTJ!!)
        }
    }

    private fun showD5() {
        val adHelper = ADHelper.getInstance()
        val property = adHelper.d5Property(D5_VISIT)
        if (property.pageID.isEmpty() || !adHelper.isD5Open()) {
            ivD5.visibility = View.GONE
            return
        }

        val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
        Glide.with(this).load(adHelper.d5ImageUrl(D5_VISIT)).apply(options).into(ivD5)

        ivD5.setOnClickListener {
            when (property.function) {
                1 -> findNavController().navigate(VisitorTipFragmentDirections.actionToExhibitorDetailFragment(property.pageID))
                2 -> { // 同期活动
                    setItemEventID(property.pageID)
                    findNavController().navigate(VisitorTipFragmentDirections.actionToEventDetailFragment(property.pageID,
                        getString(R.string.title_concurrent_event)))
                }
            }
        }
    }

}
