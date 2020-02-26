//package com.adsale.chinaplas.ui.newtech
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.adsale.chinaplas.R
//import com.adsale.chinaplas.adapters.ApplicationAdapter
//import com.adsale.chinaplas.base.BaseFilterFragment
//import com.adsale.chinaplas.data.dao.CpsDatabase
//import com.adsale.chinaplas.data.dao.NewtechRepository
//import com.adsale.chinaplas.utils.LogUtil
//import com.adsale.chinaplas.viewmodels.MainViewModel
//
///*新技术产品*/
//const val TYPE_NEW_TEC_PRODUCT = "PRD" /* 列表为 新技术产品 - 筛选 - 产品 */
//const val TYPE_NEW_TEC_APPLICATIONS = "APT" /* 列表为 新技术产品 - 筛选 - 应用 */
//const val TYPE_NEW_TEC_THEMATIC = "THS" /* 列表为 新技术产品 - 筛选 - 主题专集 */
//const val TYPE_NEW_TECH_AREA = "AREA" /* 列表为 新技术产品 - 筛选 - 新品技术 */
//
//class FilterListFragment : Fragment() {
//    private var type = ""
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewModel:NewtechFilterViewModel
//    protected lateinit var mainViewModel: MainViewModel
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.layout_recycler_view, container, false)
//        recyclerView = view.findViewById(R.id.recycler_view)
//        recyclerView.setHasFixedSize(true)
//        recyclerView.addItemDecoration(
//            DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
//        )
//        LogUtil.i("this BaseFilterFragment=${this}")
//        return view
//
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//    }
//
//
//    override fun initData() {
//        val adapter = ApplicationAdapter(listOf(), viewModel.appItemListener)
//        recyclerView.adapter = adapter
//        getNewtechList(type)
//
//        viewModel.applications.observe(this, Observer {
//            LogUtil.i("applications= ${it.size}, ${it}")
//            adapter.setList(it)
//        })
//
//
//    }
//
//    private fun getNewtechList(type: String) {
//        viewModel.setNewtechType(type)
//        viewModel.setNewtechRepository(NewtechRepository.getInstance(CpsDatabase.getInstance(requireContext()).newtechDao()))
//        viewModel.getNewtechList(type)
//        viewModel.appFilterClear()
//    }
//
////    inner class NewtechFilterListAdapter:CpsBaseAdapter<ExhApplication>{
////
////    }
//
//}