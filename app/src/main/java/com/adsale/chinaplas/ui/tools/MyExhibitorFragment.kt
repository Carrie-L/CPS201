package com.adsale.chinaplas.ui.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.adapters.MyExhibitorAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.databinding.MyExhibitorFragmentBinding
import com.adsale.chinaplas.helper.InjectionUtils
import com.adsale.chinaplas.ui.view.HELP_MY_EXHIBITOR
import com.adsale.chinaplas.ui.view.HelpDialog
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.utils.alertDialogProgress
import com.adsale.chinaplas.utils.inTransaction
import com.adsale.chinaplas.viewmodels.MyExhibitorViewModel


class MyExhibitorFragment : Fragment() {

    companion object {
        fun newInstance() = MyExhibitorFragment()
    }

    private val viewModel: MyExhibitorViewModel by viewModels {
        InjectionUtils.provideMyExhibitorViewModelFactory(requireContext())
    }
    private lateinit var binding: MyExhibitorFragmentBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MyExhibitorFragmentBinding.inflate(inflater)
        recyclerView = binding.exhibitorRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = MyExhibitorAdapter(onItemClickListener)
        recyclerView.adapter = adapter
        viewModel.exhibitors.observe(this, Observer {
            i("my it = ${it.size}")
            adapter.submitList(it)
        })

        onFABClick()
        helpPage()
    }

    private fun onFABClick() {
        binding.btnSync.setOnClickListener {
            val dialog = alertDialogProgress(requireContext(), "同步ing...")

        }
        binding.btnHall.setOnClickListener {


        }
    }

    private val onItemClickListener = OnItemClickListener { entity, pos ->
        findNavController().navigate(
            MyExhibitorFragmentDirections.actionMyExhibitorFragmentToExhibitorDetailFragment(
                (entity as Exhibitor).CompanyID))
    }

    private fun helpPage() {
        binding.ivHelp.setOnClickListener {
            val helpDialog = HelpDialog.getInstance(HELP_MY_EXHIBITOR)
            requireActivity().supportFragmentManager.inTransaction {
                helpDialog.show(fragmentManager!!, "Help")
            }
        }
    }

}
