package com.adsale.chinaplas.ui.exhibitors


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewDebug
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseListAdapter
import com.adsale.chinaplas.adapters.ExhibitorListAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.databinding.FragmentExhibitorListBinding
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.ExhibitorViewModel
import com.adsale.chinaplas.viewmodels.ExhibitorViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class ExhibitorListFragment : Fragment() {
    private lateinit var exhibitorRepository: ExhibitorRepository
    private lateinit var exhibitorViewModel: ExhibitorViewModel

    private lateinit var binding: FragmentExhibitorListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExhibitorListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentExhibitorListBinding.inflate(inflater)
        recyclerView = binding.rvExhibitor
        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.setHasFixedSize(true)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        exhibitorRepository = ExhibitorRepository.getInstance(CpsDatabase.getInstance(context!!).exhibitorDao())
        exhibitorViewModel = ViewModelProviders.of(this,
            ExhibitorViewModelFactory(requireActivity().application, exhibitorRepository))
            .get(ExhibitorViewModel::class.java)
        binding.exhibitorModel = exhibitorViewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

//        adapter = ExhibitorListAdapter(mutableListOf(), itemClickListener,exhibitorViewModel)
        adapter = ExhibitorListAdapter(itemClickListener,DiffCallback,exhibitorViewModel)
        recyclerView.adapter = adapter

        exhibitorViewModel.exhibitors.observe(this, Observer {
            LogUtil.i("exhibitors=${it?.size}")
            it?.let {
//                adapter.setList(it)
                adapter.submitList(it)
            }


        })


    }

    private val itemClickListener = OnItemClickListener { entity, pos ->
        entity as Exhibitor
        Toast.makeText(context!!, " this is $pos and name is ${entity.getCompanyName()}", Toast.LENGTH_SHORT).show()
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MarsProperty]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Exhibitor>() {
        override fun areItemsTheSame(oldItem: Exhibitor, newItem: Exhibitor): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Exhibitor, newItem: Exhibitor): Boolean {
            return oldItem.IsFavourite == newItem.IsFavourite
        }
    }


}
