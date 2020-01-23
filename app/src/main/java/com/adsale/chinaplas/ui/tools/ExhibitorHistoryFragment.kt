package com.adsale.chinaplas.ui.tools

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.ExhibitorHistory
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getCurrentTime
import com.adsale.chinaplas.viewmodels.ExhibitorHistoryViewModel
import com.adsale.chinaplas.viewmodels.ExhibitorHistoryViewModelFactory
import kotlinx.coroutines.*

/**
 */
class ExhibitorHistoryFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ExhibitorHistoryViewModel
    private lateinit var adapter: ExhibitorHistoryAdapter

    private val exhibitorDao by lazy {
        CpsDatabase.getInstance(requireContext()).exhibitorDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        LogUtil.i("onActivityCreated")

        viewModel = ViewModelProviders.of(this, ExhibitorHistoryViewModelFactory(exhibitorDao))
            .get(ExhibitorHistoryViewModel::class.java)

        adapter = ExhibitorHistoryAdapter(listOf(), itemClickListener)
        recyclerView.adapter = adapter

        viewModel.getList()

    }


    private val itemClickListener = OnItemClickListener { entity, pos ->
        entity as ExhibitorHistory
        findNavController().navigate(
            ExhibitorHistoryFragmentDirections.actionExhibitorHistotyFragmentToExhibitorDetailFragment(
                entity.CompanyID
            )
        )
    }

    override fun onResume() {
        super.onResume()

        LogUtil.i("onResume")
        viewModel.list.observe(this, Observer {
            adapter.setList(it)
        })


    }

    override fun onDetach() {
        super.onDetach()

    }


}
