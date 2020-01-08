package com.adsale.chinaplas.ui.dialog

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.CpsBaseListAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.cpsDatabase
import com.adsale.chinaplas.data.dao.RegOptionData
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.databinding.LayoutRegProfuctDialogBinding
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getScreenHeight
import com.adsale.chinaplas.utils.getScreenWidth
import com.adsale.chinaplas.utils.setRegServiceCode
import com.adsale.chinaplas.viewmodels.RegPickViewModel
import com.adsale.chinaplas.viewmodels.RegViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.lang.StringBuilder


/**
 * Created by Carrie on 2019/11/4.
 * 选择公司业务
 * todo 选择项保存在本地，下一次进来时读取，然后在列表中显示
 */
class RegProductDialog : DialogFragment() {
    private lateinit var mView: View
    private lateinit var binding: LayoutRegProfuctDialogBinding
    private lateinit var repository: RegisterRepository
    private lateinit var regViewModel: RegPickViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.transparentBgDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.let {
            it.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            /* Make us non-modal, so that others can receive touch events.
            说人话就是 取消默认的 对话框外部区域点击取消对话框事件，而是换成响应事件，不能理解则注释下面两端代码运行看效果。 */
            it.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            // ...but notify us that it happened.
            it.window!!.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)

            LogUtil.i("init RegProductDialog~~")
            binding = LayoutRegProfuctDialogBinding.inflate(LayoutInflater.from(activity), container, true)
            mView = binding.root
            repository = RegisterRepository.getInstance(cpsDatabase.countryDao(), cpsDatabase.regOptionDao())
            regViewModel = ViewModelProviders.of(requireActivity(), RegViewModelFactory(repository))
                .get(RegPickViewModel::class.java)
            binding.viewModel = regViewModel
            binding.lifecycleOwner = this
        }
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWindow()
        initFlexBoxView()
        initProductListView()
    }

    private fun initFlexBoxView() {
        val rvFlexLabel: RecyclerView = binding.rvProductLabels
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.flexWrap = FlexWrap.WRAP
        layoutManager.justifyContent = JustifyContent.FLEX_START
        rvFlexLabel.layoutManager = layoutManager

        val adapter = RegLabelAdapter(mutableListOf(), regViewModel.labelItemClickListener)
        val code = StringBuilder()
        regViewModel.serviceLabels.observe(this, Observer {
            adapter.setList(it)
            LogUtil.i("productLabels.observe: ${it.size}")

            // save to sp service code
            code.clear()
            for (item in it) {
                if (code.isNotEmpty()) {
                    code.append(",")
                }
                code.append(item.DetailCode)
            }
            setRegServiceCode(code.toString())

        })
        rvFlexLabel.adapter = adapter
    }

    private fun initProductListView() {
        val recyclerView = binding.rvList
        adapter = RegProductAdapter(listOf(), regViewModel.onProductListItemClickListener)
        recyclerView.adapter = adapter
        regViewModel.services.observe(this, Observer {
            adapter.setList(it)
        })
        regViewModel.dialogDismiss.observe(this, Observer { isDismiss ->
            if (isDismiss) {
                dialog?.dismiss()
                regViewModel.dialogDismiss.value = false
            }
        })
    }

    private lateinit var adapter: RegProductAdapter

    class RegProductAdapter(private var list: List<RegOptionData>, onItemClickListener: OnItemClickListener) : CpsBaseAdapter<RegOptionData>(list, onItemClickListener) {

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_reg_product
        }

        override fun setList(newList: List<RegOptionData>) {
            list = newList
            super.setList(newList)
        }
    }

    class RegLabelAdapter(private var list: List<RegOptionData>, labelItemClickListener: OnItemClickListener) : CpsBaseAdapter<RegOptionData>(list, labelItemClickListener) {

        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_reg_lable
        }

        override fun setList(newList: List<RegOptionData>) {
            list = newList
            super.setList(newList)
        }
    }

    val labelDiffCallback = object : CpsBaseListAdapter.DiffCallback<RegOptionData>() {
        override fun areItemsTheSame(oldItem: RegOptionData, newItem: RegOptionData): Boolean {
            return oldItem.objectId == newItem.objectId
        }
    }

    private fun setWindow() {
        val window = dialog?.window
        val wl = window?.attributes
        wl?.let {
            wl.width = getScreenWidth()
            wl.height = (getScreenHeight() / 2)
            wl.gravity = Gravity.BOTTOM
            window.attributes = wl
        }
    }

    override fun onDetach() {
        super.onDetach()


    }


}



