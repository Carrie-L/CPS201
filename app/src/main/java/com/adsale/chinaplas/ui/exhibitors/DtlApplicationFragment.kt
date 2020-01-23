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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.ApplicationAdapter
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.ExhApplication
import com.adsale.chinaplas.data.dao.ExhIndustry
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.utils.EXHIBITOR_APPLICATION
import com.adsale.chinaplas.utils.EXHIBITOR_INDUSTRY
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.ExhibitorDtlViewModel

/**
 * A simple [Fragment] subclass.
 */
class DtlApplicationFragment constructor(private val viewModel: ExhibitorDtlViewModel) : Fragment() {
    private var isInited = false
    private var lastView: View? = null
    private lateinit var recyclerView: RecyclerView
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
            val adapter = ApplicationAdapter(listOf(), OnItemClickListener { entity, pos ->
                viewModel.setItemNavigationValue(KV(EXHIBITOR_APPLICATION, (entity as ExhApplication).IndustryID))
            })
            recyclerView.adapter = adapter
            viewModel.applications.observe(this, Observer {
                LogUtil.i("observe - applications: ${it.size}")
                if (it.isNotEmpty()) {
                    adapter.setList(it)
                }else{
                    tvNoData.visibility = View.VISIBLE
                }
            })
//            isInited = true
//        }


    }


}
