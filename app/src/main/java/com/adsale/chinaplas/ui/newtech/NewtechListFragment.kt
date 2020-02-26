package com.adsale.chinaplas.ui.newtech


import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.NewtechAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.NewtechProductInfo
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.data.entity.D7
import com.adsale.chinaplas.databinding.FragmentNewtechListBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.util.*


/**
 * 新技术产品列表
 */
class NewtechListFragment : Fragment() {
    private lateinit var binding: FragmentNewtechListBinding
    private lateinit var viewModel: NewtechViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewtechAdapter
    private lateinit var adHelper: ADHelper
    private var d7List = listOf<D7>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewtechListBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        var filter = ""
        arguments?.let {
            if (it.containsKey("filter")) {
                LogUtil.i("containsKey")
                filter = NewtechListFragmentArgs.fromBundle(it).filter.toString()
                LogUtil.i("filter=$filter")
            } else {
                LogUtil.i("not containsKey")
            }
        }

        adHelper = ADHelper.getInstance(requireActivity().application)
        d7List = adHelper.d7List()

        initViewModel()

        if (!TextUtils.isEmpty(filter)) {
            LogUtil.i("filter not empty, 筛选啦")
            viewModel.getFilterList(filter)
            viewModel.getAllProductInfoList(false)
        } else {
            viewModel.getAllProductInfoList(true)
        }

        viewModel.list.observe(this, Observer {
            val size = it.size
            if (adHelper.isD7Open() && d7List.isNotEmpty() && size > 0) {
                var d7NewtechEntity: NewtechProductInfo
                for ((i, d7Item) in d7List.withIndex()) {
                    d7NewtechEntity = adHelper.getADProductEntity(d7Item)
                    if (size >= ((i + 1) * 2)) {
                        it.add((i + 1) * 2, d7NewtechEntity)
                    }
                }
            }
            adapter.setList(it)
        })

        search()
        onClick()
    }

    private fun search() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    viewModel.reset()
                } else {
                    viewModel.search(editable.toString().toLowerCase(Locale.ENGLISH))
                }
            }
        })
    }

    private fun onClick() {
        viewModel.clickState.observe(this, Observer {
            LogUtil.i("clickState=$it")
            if (it == 0) {
                return@Observer
            }
            if (it == NEWTECH_CLICK_FILTER) {
                findNavController().navigate(R.id.newtechFilterFragment)
            } else if (it == NEWTECH_CLICK_RESET) {
                binding.etSearch.setText("")
                hideInput()
                viewModel.reset()
                LogUtil.i("reset$$$$$$$$")
            } else if (it == NEWTECH_CLICK_HELP) {
                // TODO


            }
            viewModel.resetClickState()
        })
    }

    private fun showD7() {
        adHelper.d7List()
    }

    private fun initView() {
        recyclerView = binding.rvNewTech
        recyclerView.setHasFixedSize(true)

        val options =
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE).placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
        adapter = NewtechAdapter(listOf(), itemClickListener, options)
        recyclerView.adapter = adapter
    }

    private val itemClickListener = OnItemClickListener { entity, pos ->
        entity as NewtechProductInfo
        LogUtil.i("itemClickListener: ${entity.toString()}")
        findNavController().navigate(NewtechListFragmentDirections.actionNewtechListFragmentToNewtechDetailFragment(
            entity.RID))
    }

    private fun getFilterArgs() {

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this,
            NewtechViewModelFactory(NewtechRepository.getInstance(CpsDatabase.getInstance(requireContext()).newtechDao())))
            .get(NewtechViewModel::class.java)
        binding.model = viewModel
        binding.lifecycleOwner = this
    }

    private fun hideInput() {
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.root.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        hideInput()
    }


}
