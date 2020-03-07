package com.adsale.chinaplas.ui.exhibitors


import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.ADScrollAdapter
import com.adsale.chinaplas.adapters.ExhibitorListAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.entity.Property
import com.adsale.chinaplas.databinding.FragmentExhibitorListBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.ui.view.RecyclerViewScrollTo
import com.adsale.chinaplas.ui.view.SideBarView
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.ExhibitorViewModel
import com.adsale.chinaplas.viewmodels.ExhibitorViewModelFactory
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import kotlinx.coroutines.*
import java.util.*

/**
 * 展商列表，包含：
 * 1. 全部展商
 * 2. 搜索展商
 * 3. 筛选展商 （展商筛选跳转）
 * 4. 包含某产品/应用行业/新技术产品的展商 （展商详情跳转）
 * 5. 添加日历显示全部展商  （添加日历跳转）
 *
 * 参数  的意思：
 * @param key 跳转来的类型，
 * @param value 类型值
 * 举例：
 * @sample 应用行业   [EXHIBITOR_APPLICATION] [applicationId]
 * @sample 产品          [EXHIBITOR_INDUSTRY] [industryId]
 * @sample 筛选          [EXHIBITOR_FILTER] [filterSql]
 * @sample 默认全部   [EXHIBITOR_ALL] []
 *
 */
class ExhibitorListFragment : Fragment() {
    private lateinit var exhibitorRepository: ExhibitorRepository
    private lateinit var exhibitorViewModel: ExhibitorViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var rvScrollTo: RecyclerViewScrollTo

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

    private lateinit var binding: FragmentExhibitorListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExhibitorListAdapter
    private var type: String = ""   // key
    private var value: String? = ""  // value
    private var filterSql: String? = ""
    private var industryId: String? = ""
    private var applicationId: String? = ""

    private var isViewInited = false
    private var lastView: View? = null
    private lateinit var sideBarView: SideBarView
    private lateinit var rvD3: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (lastView == null) {
            LogUtil.i("---lastView == null---")
            binding = FragmentExhibitorListBinding.inflate(inflater)
            recyclerView = binding.rvExhibitor
            val layoutManager = LinearLayoutManager(context!!)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            lastView = binding.root
            sideBarView = binding.viewSideData
            rvScrollTo = RecyclerViewScrollTo(layoutManager, recyclerView)
            sideBarView.setAdapter(sideClickListener)
        } else {
            LogUtil.i("---lastView nottttt null---")
        }
        return lastView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!isViewInited) {
            arguments?.let {
                type = ExhibitorListFragmentArgs.fromBundle(it).key
                if (TextUtils.isEmpty(type)) {
                    type = EXHIBITOR_ALL
                }
                value = ExhibitorListFragmentArgs.fromBundle(it).value
                if (!TextUtils.isEmpty(value)) {
                    LogUtil.i("value= $value")
                    when (type) {
                        EXHIBITOR_FILTER -> filterSql = value
                        EXHIBITOR_APPLICATION -> applicationId = value
                        EXHIBITOR_INDUSTRY -> industryId = value
                    }
                }
            }
            LogUtil.i("filterSql= $filterSql")
            LogUtil.i("applicationId= $applicationId")
            LogUtil.i("industryId= $industryId")
            isViewInited = true
            mainViewModel = ViewModelProviders.of(
                requireActivity(), MainViewModelFactory(
                    requireActivity().application,
                    MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
                )
            ).get(MainViewModel::class.java)
            initData()
            setAdapterList()

            isViewInited = true

            exhibitorViewModel.navigateFilter.observe(this, Observer { navigation ->
                if (navigation) {
                    findNavController().navigate(ExhibitorListFragmentDirections.actionExhibitorListFragmentToMenuExhibitors())
                    exhibitorViewModel.finishNavigateFilter()
                }
            })
            showD3()
        }
        search()

        exhibitorViewModel.adRollIndex.observe(this, Observer {
            if (it != -1) {
                LogUtil.i("d3RollIndex: it=$it")
                rvD3.smoothScrollToPosition(it)
            }
        })

        /**
         *
         * @param K: [position]
         * @param V: [hasUpdate]
         */
//        exhibitorViewModel.isItemUpdate.observe(this, Observer { kv ->
//            if (kv.K != -1 && kv.V as Boolean) {
//                LogUtil.i("item有更新: ${kv.toString()}")
//                val pos = kv.K as Int
//                val entity = exhibitorViewModel.exhibitors.value!![pos]
//                entity.IsFavourite = if (entity.IsFavourite == 1) 0 else 1
//                exhibitorViewModel.exhibitors.value!![pos] = entity
//                exhibitorViewModel.resetItemUpdate()
//            }
//        })


//        exhibitorViewModel.itemHasUpdate.observe(this, Observer { update ->
//            if (update && exhibitorViewModel.itemPos.value != -1) {
//                LogUtil.i("item有更新")
//                val pos = exhibitorViewModel.itemPos.value!!
//                val entity = exhibitorViewModel.exhibitors.value!![pos]
//                entity.IsFavourite = if (entity.IsFavourite == 1) 0 else 1
//                entity.isStared.set(true)
//                exhibitorViewModel.exhibitors.value!![pos] = entity
//                exhibitorViewModel.resetItemUpdate()
//            }
//        })

        binding.ivHelper.setOnClickListener {


        }

    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume: ${exhibitorViewModel.itemPos.value}")
        if (exhibitorViewModel.itemPos.value != -1) {
            uiScope.launch {
                val pos = exhibitorViewModel.itemPos.value!!
                var entity = exhibitorViewModel.exhibitors.value!![pos]
                entity = exhibitorViewModel.getItemCompanyFromDB(entity.CompanyID)
                LogUtil.i("entity=${entity.toString()}")
                val list = exhibitorViewModel.exhibitors.value
                entity.isStared.set(entity.IsFavourite == 1)
                list!![pos] = entity
                exhibitorViewModel.exhibitors.value = list
                exhibitorViewModel.itemPos.value = -1
            }
        }

        exhibitorViewModel.exhibitors.observe(this, Observer {
            LogUtil.i("exhibitors=${it?.size}")
            it?.let {
                adapter.setList(it)
                // 侧边bar
                sideBarView.setData(exhibitorViewModel.setSideBarList())
            }
        })
    }


    private fun initData() {
        exhibitorRepository = ExhibitorRepository.getInstance(
            CpsDatabase.getInstance(context!!).exhibitorDao(),
            CpsDatabase.getInstance(context!!).industryDao(),
            CpsDatabase.getInstance(context!!).applicationDao()
        )
        exhibitorViewModel = ViewModelProviders.of(
            this,
            ExhibitorViewModelFactory(requireActivity().application, exhibitorRepository)
        )
            .get(ExhibitorViewModel::class.java)
        binding.exhibitorModel = exhibitorViewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        exhibitorViewModel.getExhibitorList(type, value ?: "")

        exhibitorViewModel.starDialog.observe(this, Observer {
            if (it) {
                alertDialogConfirmTwo(requireContext(), R.string.login_first_add_exhibitor,
                    DialogInterface.OnClickListener { dialog, which ->
                        exhibitorViewModel.starDialog.value = false
                        findNavController().navigate(R.id.myChinaplasLoginFragment)
                    })
            }
        })
    }

    private fun setAdapterList() {
        adapter = ExhibitorListAdapter(mutableListOf(), itemClickListener, exhibitorViewModel)
//        adapter = ExhibitorListAdapter(itemClickListener,DiffCallback,exhibitorViewModel)
        recyclerView.adapter = adapter
    }

    private fun search() {
        exhibitorViewModel.searchText.observe(this, Observer { text ->
            LogUtil.i("searchText--${text}")
            if (TextUtils.isEmpty(text)) {
                exhibitorViewModel.resetList()
            } else {
                exhibitorViewModel.queryExhibitorsLocal(text.toLowerCase(Locale.ENGLISH))
            }
        })
    }

    private val itemClickListener = OnItemClickListener { entity, pos ->
        entity as Exhibitor
        hideInput()
//        exhibitorViewModel.itemEntity.value = entity
//        exhibitorViewModel.setItemHasUpdate(pos)
        exhibitorViewModel.itemPos.value = pos
        findNavController().navigate(
            ExhibitorListFragmentDirections.actionExhibitorListFragmentToExhibitorDetailFragment(
                entity.CompanyID
            )
        )
//        binding.etSearch.setText("")
    }

    private fun showD3() {
        rvD3 = binding.rvAd
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvD3.setHasFixedSize(true)
        rvD3.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvD3)

        val adHelper = ADHelper.getInstance()

//        val params = ConstraintLayout.LayoutParams(getScreenWidth(), adHelper.getADHeight())
//        params.bottomToBottom = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
//        params.topToBottom = R.id.rv_exhibitor
//        rvD3.layoutParams = params

        val list = adHelper.d3Property()
        val adapter = ADScrollAdapter(list, OnItemClickListener { entity, pos ->
            entity as Property
            findNavController().navigate(
                ExhibitorListFragmentDirections.actionExhibitorListFragmentToExhibitorDetailFragment(
                    entity.pageID
                )
            )
        })
        rvD3.adapter = adapter

        exhibitorViewModel.setD3Size(list.size)
        exhibitorViewModel.startAdTimer()
    }

    private fun hideInput() {
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.root.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }


    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MarsProperty]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<Exhibitor>() {
        override fun areItemsTheSame(oldItem: Exhibitor, newItem: Exhibitor): Boolean {
            LogUtil.i("oldItem=${oldItem.toString()}")
            LogUtil.i("newItem=${newItem.toString()}")
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Exhibitor, newItem: Exhibitor): Boolean {
            return oldItem.IsFavourite == newItem.IsFavourite
        }
    }

    private var sideTemps = mutableListOf<String>()
    private fun setSideBar(list: List<Exhibitor>) {
        // 侧边bar ， 去重
        sideTemps.clear()
        for (entity in list) {
            if (exhibitorViewModel.isSortBySZ.get()) {
                sideTemps.add(entity.getSort())
            } else {
                sideTemps.add(entity.HallNo!!)
            }
        }
        sideTemps = sideTemps.distinct() as MutableList<String>
        if (!exhibitorViewModel.isSortBySZ.get()) {
            sideTemps.sort()
        }
        LogUtil.i("sideTemps 1st = ${sideTemps.size},  ${sideTemps.toString()}")
        sideBarView.setData(sideTemps)
    }


    val sideClickListener = OnItemClickListener { entity, pos ->
        LogUtil.i("itemClickListener=$entity")
        scrollToPosition(entity as String)
    }

    private fun scrollToPosition(sort: String) {
        with(exhibitorViewModel) {
            exhibitors.value?.let {
                if (it.isEmpty()) {
                    return@let
                }
                for ((i, entity) in it.withIndex()) {
                    if (isSortBySZ.get()) {
                        if (entity.getSort() == sort) {
                            rvScrollTo.scroll(i)
                            LogUtil.i("scroll to: $i, $sort")
                            break
                        }
                    } else {
                        if (entity.HallNo == sort) {
                            rvScrollTo.scroll(i)
                            LogUtil.i("scroll to: $i, $sort")
                            break
                        }
                    }
                }
            }
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        super.onPause()
        exhibitorViewModel.stopTimer()
    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    override fun onStop() {
//        super.onStop()
//        exhibitorViewModel.stopTimer()
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onStart() {
        super.onStart()
        exhibitorViewModel.startAdTimer()
    }

}
