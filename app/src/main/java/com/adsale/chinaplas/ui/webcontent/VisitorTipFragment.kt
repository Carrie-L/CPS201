package com.adsale.chinaplas.ui.webcontent


import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.adapters.TipAdapter
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.WebContent
import com.adsale.chinaplas.data.dao.WebContentRepository
import com.adsale.chinaplas.utils.dp2px
import com.adsale.chinaplas.utils.getSpanCount
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
class VisitorTipFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var baiduTJ: String? = "GeneralInformation"
    private var job = Job()
    private var list = MutableLiveData<List<WebContent>>()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)
    private lateinit var webContentRepository: WebContentRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_visitor_tip, container, false)
        recyclerView = view.findViewById(R.id.rv_tips)
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


    }

    private suspend fun getWebContents(): List<WebContent> {
        return withContext(Dispatchers.IO) {
            webContentRepository.getWebContents(baiduTJ!!)
        }
    }

}
