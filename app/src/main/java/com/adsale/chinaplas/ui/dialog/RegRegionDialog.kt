package com.adsale.chinaplas.ui.dialog

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.SimpleItemAnimator
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.cpsDatabase
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.databinding.LayoutRegDialogBinding
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getScreenHeight
import com.adsale.chinaplas.utils.getScreenWidth
import com.adsale.chinaplas.viewmodels.RegRegionViewModel
import com.adsale.chinaplas.viewmodels.RegViewModelFactory
import kotlinx.coroutines.*


/**
 * Created by Carrie on 2019/11/4.
 * 选择国家/地区
 */
class RegRegionDialog : DialogFragment() {
    private lateinit var mView: View
    private lateinit var binding: LayoutRegDialogBinding
    private lateinit var repository: RegisterRepository
    private lateinit var regionViewModel: RegRegionViewModel
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.transparentBgDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.let {
            it.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            /* Make us non-modal, so that others can receive touch events.
            说人话就是 取消默认的 对话框外部区域点击取消对话框事件，而是换成响应事件，不能理解则注释下面两端代码运行看效果。 */
            it.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
            // ...but notify us that it happened.
            it.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            )
            it.window!!.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

            LogUtil.i("init RegRegionDialog~~")
            binding =
                LayoutRegDialogBinding.inflate(LayoutInflater.from(activity), container, false)
            mView = binding.root
            repository =
                RegisterRepository.getInstance(cpsDatabase.countryDao(), cpsDatabase.regOptionDao())
            regionViewModel =
                ViewModelProviders.of(requireActivity(), RegViewModelFactory(repository))
                    .get(RegRegionViewModel::class.java)
            binding.regionViewModel = regionViewModel
            binding.lifecycleOwner = this

            regionViewModel.initDialog()

        }

        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setWindow()

        initData()
    }

    private lateinit var adapter: RegDialogAdapter
    private fun initData() {
        val recyclerView = binding.rvList
        // 取消局部刷新动画
        recyclerView.itemAnimator!!.addDuration = 0
        recyclerView.itemAnimator!!.changeDuration = 0
        recyclerView.itemAnimator!!.moveDuration = 0
        recyclerView.itemAnimator!!.removeDuration = 0
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        regionViewModel.indexClicked.value = 0
        adapter = RegDialogAdapter(listOf(), regionViewModel.itemClickListener)
        recyclerView.adapter = adapter
        regionViewModel.list.observe(this, Observer {
            it?.let {
                if (regionViewModel.refreshList) {
                    adapter.setList(it)
                } else {
                    adapter.setListNoChange(it)
                    uiScope.launch {
                        refreshItem(regionViewModel.currentPos)
                        refreshItem(regionViewModel.prePos)
                    }
                }
            }
        })
        regionViewModel.dialogDismiss.observe(this, Observer { isDismiss ->
            if (isDismiss) {
                if (regionViewModel.isChinaSelected) {
                    if (regionViewModel.provinceText.value == regionViewModel.selectTitle) {
                        Toast.makeText(requireContext(), "请选择省份", Toast.LENGTH_SHORT).show()
                        return@Observer
                    }
                    if (regionViewModel.cityText.value.equals(regionViewModel.selectTitle)) {
                        Toast.makeText(requireContext(), "请选择城市", Toast.LENGTH_SHORT).show()
                        return@Observer
                    }
//                    regionViewModel.region.value =
//                        regionViewModel.regionText.value + " " + regionViewModel.provinceText.value + " " + regionViewModel.cityText.value
//                    LogUtil.i("region = ${regionViewModel.region.value}")
                    dialog?.dismiss()
                } else {
                    if (regionViewModel.regionText.value != regionViewModel.selectTitle) {
//                        regionViewModel.region.value = regionViewModel.regionText.value
                        dialog?.dismiss()
                    }
                }
                regionViewModel.dialogDismiss.value = false
            }
        })

    }

    private suspend fun refreshItem(pos: Int) {
        withContext(Dispatchers.Main) {
            adapter.notifyItemChanged(pos)
        }
    }


    class RegDialogAdapter(private var list: List<CountryJson>, onItemClickListener: OnItemClickListener) :
        CpsBaseAdapter<CountryJson>(list, onItemClickListener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_reg_region
        }

        override fun setList(newList: List<CountryJson>) {
            list = newList
            super.setList(newList)
        }

        override fun setListNoChange(newList: List<CountryJson>) {
            list = newList
            super.setListNoChange(newList)
        }
    }

    private fun setWindow() {
        val window = dialog?.window
        val wl = window?.attributes
        wl?.let {
            wl.width = getScreenWidth()
            wl.height = getScreenHeight() / 2
            wl.gravity = Gravity.BOTTOM
            window.attributes = wl
        }
    }

    override fun onDetach() {
        super.onDetach()
        regionViewModel.indexClicked.value = 0
        regionViewModel.refreshList = true
        regionViewModel.isChinaSelected = false
        //                regionViewModel.provinceVisible.value = View.GONE
        //                regionViewModel.cityVisible.value = View.GONE


    }


}



