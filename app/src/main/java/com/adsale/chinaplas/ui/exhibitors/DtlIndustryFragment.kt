package com.adsale.chinaplas.ui.exhibitors


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.ExhIndustry
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.utils.EXHIBITOR_INDUSTRY
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.ExhibitorDtlViewModel


/**
 * A simple [Fragment] subclass.
 */
class DtlIndustryFragment constructor(private val viewModel: ExhibitorDtlViewModel) : Fragment() {
    private var isInited = false
    private var lastView: View? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IndustryAdapter
    private lateinit var tvNoData: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        if (lastView == null) {
            val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
            recyclerView = view.findViewById(R.id.recycler_view)
            tvNoData = view.findViewById(R.id.tv_no_data)
            recyclerView.setHasFixedSize(true)
            recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
            lastView = view
//        }
        return lastView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        if (!isInited) {
            adapter = IndustryAdapter(listOf(), OnItemClickListener { entity, pos ->
                viewModel.setItemNavigationValue(KV(EXHIBITOR_INDUSTRY, (entity as ExhIndustry).CatalogProductSubID))
            })
            recyclerView.adapter = adapter
            viewModel.industries.observe(this, Observer {
                LogUtil.i("observe - industries: ${it.size}")
                if (it.isNotEmpty()) {
                    adapter.setList(it)
                }else{
                    tvNoData.visibility = View.VISIBLE
                }
            })
//            isInited = true
//        }

    }

    class IndustryAdapter(private var list: List<ExhIndustry>, listener: OnItemClickListener) :
        CpsBaseAdapter<ExhIndustry>(list, listener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_exh_dtl_industry
        }

        override fun setList(newList: List<ExhIndustry>) {
            this.list = newList
            super.setList(newList)
        }

    }


}
