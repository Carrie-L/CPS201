package com.adsale.chinaplas.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.adapters.SearchAdapter
import com.adsale.chinaplas.cpsDatabase
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.HtmlText
import com.adsale.chinaplas.data.dao.HtmlTextRepository
import com.adsale.chinaplas.data.entity.SearchTag
import com.adsale.chinaplas.databinding.FragmentGlobalSearchBinding
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.GlobalSearchViewModelFactory
import com.adsale.chinaplas.viewmodels.GlobalViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

/**
 * A simple [Fragment] subclass.
 */
class GlobalSearchFragment : Fragment() {
    private lateinit var binding: FragmentGlobalSearchBinding
    //    private lateinit var flexLayout: FlexboxLayout
    private lateinit var rvFlex: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: GlobalViewModel
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentGlobalSearchBinding.inflate(inflater)
        rvFlex = binding.flexLayout
        etSearch = binding.etSearch
        recyclerView = binding.rvResult
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSearchTag()
        initSearchList()

        val datebase = CpsDatabase.getInstance(requireContext())
        viewModel = ViewModelProviders.of(this,
            GlobalSearchViewModelFactory(HtmlTextRepository.getInstance(datebase.htmlTextDao()),
                datebase.exhibitorDao(),
                cpsDatabase.newtechDao(),
                cpsDatabase.seminarDao())).get(GlobalViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        etSearch.requestFocus()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    recyclerView.visibility = View.GONE
                    clearText()
                    viewModel.resultList.value = listOf()
                } else {
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        viewModel.resultList.observe(this, Observer {
            LogUtil.i("===resultList=${it.size}")
            if (it.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
            } else {
                if (viewModel.isSearching.value == true) {
                    binding.tvNoData.visibility = View.VISIBLE
                } else {
                    binding.tvNoData.visibility = View.GONE
                }
            }
            adapter.setList(it)
        })

        binding.btnSearch.setOnClickListener {
            if (etSearch.text.toString().isNotEmpty()) {
                viewModel.isSearching.value = true
                viewModel.searchHtmlText("%${etSearch.text.toString()}%")
            }
            hideInput(requireContext(), binding.root.windowToken)
        }

        binding.btnClear.setOnClickListener {
            etSearch.setText("")
            clearText()
            recyclerView.visibility = View.GONE
            hideInput(requireContext(), binding.root.windowToken)
        }


    }

    private fun clearText() {
        viewModel.isSearching.value = false
        binding.tvNoData.visibility = View.GONE
    }

    private fun initSearchList() {
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        adapter = SearchAdapter(listOf(), resultItemClickListener)
        recyclerView.adapter = adapter
    }

    private val resultItemClickListener = OnItemClickListener { entity, pos ->
        entity as HtmlText
        when (entity.groupID) {
            SEARCH_GROUP_EVENT -> findNavController().navigate(GlobalSearchFragmentDirections.actionToEventDetailFragment(
                entity.id,
                entity.getTitle()))
            SEARCH_GROUP_WEB_CONTENT -> findNavController().navigate(GlobalSearchFragmentDirections.actionToWebContentFragment(
                entity.id,
                entity.getTitle()))
            SEARCH_GROUP_EXHIBITOR -> findNavController().navigate(GlobalSearchFragmentDirections.actionToExhibitorDetailFragment(
                entity.id))
            SEARCH_GROUP_SEMINAR -> {
                setItemSeminarEventID(entity.id.toInt())
                findNavController().navigate(GlobalSearchFragmentDirections.actionToSeminarDetailFragment(entity.id.toInt()))
            }
            SEARCH_GROUP_NEW_TECH -> findNavController().navigate(GlobalSearchFragmentDirections.actionToNewtechDetailFragment(
                entity.id))
        }
    }

    private fun setSearchTag() {
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rvFlex.layoutManager = layoutManager

        val tags = parseListJson(SearchTag::class.java, readFilesDirFile(TXT_SEARCH_TAG)) as List<SearchTag>
        LogUtil.i("tags = ${tags?.size},,, ${tags.toString()}")

        val adapter = TagAdapter(tags, OnItemClickListener { entity, pos ->
        })
        rvFlex.adapter = adapter
    }

    inner class TagAdapter(list: List<SearchTag>, itemClickListener: OnItemClickListener) :
        CpsBaseAdapter<SearchTag>(list, itemClickListener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_search_tag
        }
    }

    override fun onDestroy() {
        if (binding?.root?.windowToken != null) {
            hideInput(requireContext(), binding.root.windowToken)
        }
        super.onDestroy()
    }


}
