package com.adsale.chinaplas.ui.newtech


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.BR
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.NewtechProductInfo
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.databinding.FragmentNewtechDetailListBinding
import com.adsale.chinaplas.network.NEW_TECH_BASE_URL
import com.adsale.chinaplas.viewmodels.NewtechViewModel
import com.adsale.chinaplas.viewmodels.NewtechViewModelFactory
import com.baidu.speech.utils.LogUtil

/**
 * 新技术产品详情
 */
class NewtechDetailFragment : Fragment() {
    //    private lateinit var binding: FragmentNewtechDetailBinding
    private lateinit var viewModel: NewtechViewModel
    private lateinit var binding: FragmentNewtechDetailListBinding
    private lateinit var recyclerView: RecyclerView
    private var rid: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewtechDetailListBinding.inflate(inflater)
        recyclerView = binding.rvNewtechDetail
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        arguments?.let {
            rid = NewtechDetailFragmentArgs.fromBundle(it).id
            LogUtil.i("rid=$rid")
//            viewModel.getItemInfo(rid)
        }

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        val adapter = DetailListAdapter(listOf(), null)
        recyclerView.adapter = adapter

        viewModel.getAllProductInfoList(true)
        viewModel.list.observe(this, Observer {
            adapter.setList(it)

            // 滑动到点击的那页
            var pos = 0
            for ((i, entity) in it.withIndex()) {
                if (entity.RID == rid) {
                    pos = i
                    break
                }
            }
            recyclerView.scrollToPosition(pos)
        })

        setClick()
    }

    private fun setClick() {
        viewModel.imageClick.observe(this, Observer {
            if (it.isNotEmpty()) {
                NavHostFragment.findNavController(this).navigate(
                    NewtechDetailADFragmentDirections.actionToImageFragment("$NEW_TECH_BASE_URL${it}"))
                viewModel.imageClick.value = ""
            }
        })
        viewModel.boothClick.observe(this, Observer {
            if (it.isNotEmpty()) {
                // todo
                viewModel.boothClick.value = ""
            }
        })
    }

    inner class DetailListAdapter(list: List<NewtechProductInfo>,
                                  itemClickListener: OnItemClickListener?) :
        CpsBaseAdapter<NewtechProductInfo>(list, itemClickListener) {

        override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
            holder.binding.setVariable(BR.viewModel, viewModel)
            super.onBindViewHolder(holder, position)
        }

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.fragment_newtech_detail
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this,
            NewtechViewModelFactory(NewtechRepository.getInstance(CpsDatabase.getInstance(requireContext()).newtechDao())))
            .get(NewtechViewModel::class.java)
    }


}
