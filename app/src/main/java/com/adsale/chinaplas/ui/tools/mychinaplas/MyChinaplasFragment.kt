package com.adsale.chinaplas.ui.tools.mychinaplas


import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.MyChinaplasAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.entity.MyChinaplasEntity
import com.adsale.chinaplas.databinding.FragmentMyChinaplasBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.helper.D5_GENERATION
import com.adsale.chinaplas.helper.D5_MYCHINAPLAS
import com.adsale.chinaplas.network.MY_CHINAPLAS_HOME_URL
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.MyChinaplasViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * A simple [Fragment] subclass.
 * todo: 1. sync    2. 样式调整，item背景颜色,icon大小，logout图标   3. 获取发票Url
 * 预登记 -> guid，  登录 -> member id。
 * 因此预登记和登录是两个操作，登录状态不能一起。
 * 最好是，当没有guid时，隐藏[获取发票],而MyChinaplas 登录下，有一个跳转去预登记的链接
 *
 */
class MyChinaplasFragment : BaseFragment() {
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MyChinaplasViewModel::class.java)
    }

    private lateinit var binding: FragmentMyChinaplasBinding
    private lateinit var ivD5: ImageView

    override fun initedView(inflater: LayoutInflater) {
        binding = FragmentMyChinaplasBinding.inflate(inflater, baseFrame, true)
        ivD5 = binding.ivD5
    }

    override fun initView() {

    }

    override fun initedData() {
        val list = mutableListOf<MyChinaplasEntity>()

        val entity1 = MyChinaplasEntity("1", getString(R.string.cps_home), R.drawable.ic_cps_home)
        val entity2 = MyChinaplasEntity("2", getString(R.string.cps_my_info), R.drawable.ic_cps_my)
        val entity3 = MyChinaplasEntity(
            "my_exhibitor",
            getString(R.string.tool_my_exhibitor),
            R.drawable.ic_tool_my_exhibitor
        )
        val entity4 = MyChinaplasEntity(
            "sync",
            getString(R.string.cps_sync_exhibitor),
            R.drawable.ic_cps_sync
        )
        val entity6 = MyChinaplasEntity("logout", getString(R.string.logout), R.drawable.ic_logout)
        list.add(entity1)
        list.add(entity2)
        list.add(entity3)
        list.add(entity4)

        val recyclerView = binding.rvMyCps
        val layoutManager = GridLayoutManager(context, getSpanCount())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val adapter = MyChinaplasAdapter(list, onItemClickListener)
        recyclerView.adapter = adapter

        binding.tvLogout.setOnClickListener {
            logout()
        }

        showD5()
    }

    override fun initData() {
        isBackCustom = true
    }

    /**
     * 如果从 [RegisterPre] 跳转而来，没有经过 [menu_tool]，则 [popBackStack] 不会返回到 [menu_tool]
     * 因此需要判断
     */
    override fun back() {
        val findToolFragment = mainViewModel.findFragmentIdAndRemoveAllBeforeIt(R.id.menu_tool)
        if (findToolFragment) {
            NavHostFragment.findNavController(this).popBackStack(R.id.menu_tool, false)
        } else {
            NavHostFragment.findNavController(this)
                .navigate(MyChinaplasFragmentDirections.actionMyChinaplasFragmentToMenuTool())
        }
        LogUtil.i("findToolFragment-----$findToolFragment")
        resetBackDefault()
    }

    private val onItemClickListener = OnItemClickListener { entity, pos ->
        when ((entity as MyChinaplasEntity).PageID) {
            "2" -> {
                resetBackDefault()
                findNavController().navigate(R.id.myLoginedFragment)
            }
            "my_exhibitor" -> {
                resetBackDefault()
                findNavController().navigate(R.id.myExhibitorFragment)
            }
            "sync" -> {
                // todo 我的参展商
                Toast.makeText(requireContext(), "TBC", Toast.LENGTH_SHORT).show()
            }
            else -> {
                resetBackDefault()
                val homeUrl = String.format(MY_CHINAPLAS_HOME_URL, getLangStr(), getToken())
                toWebView(homeUrl)
            }
        }
    }

    private fun toWebView(url: String) {
        LogUtil.i("url=$url")
        mainViewModel.resetBackDefault()
        NavHostFragment.findNavController(this)
            .navigate(
                MyChinaplasFragmentDirections.actionMyChinaplasFragmentToWebViewFragment(
                    url,
                    getString(R.string.title_my_chinaplas)
                )
            )
    }

    private fun toWebView() {
        val homeUrl = String.format(MY_CHINAPLAS_HOME_URL, getLangStr(), getToken())
        LogUtil.i("homeUrl=$homeUrl")
        mainViewModel.resetBackDefault()
        NavHostFragment.findNavController(this)
            .navigate(
                MyChinaplasFragmentDirections.actionMyChinaplasFragmentToWebViewFragment(
                    homeUrl,
                    getString(R.string.title_my_chinaplas)
                )
            )
    }

    private fun logout() {
        alertDialogTwoButton(context!!,
            R.string.logout_message,
            R.string.logout,
            R.string.cancel,
            DialogInterface.OnClickListener { dialog, which ->
                resetLoginInfo()
                mainViewModel.isLogin.value = false
                val findLoginFragment =
                    mainViewModel.findFragmentIdAndRemoveAllBeforeIt(R.id.myChinaplasLoginFragment)
                if (findLoginFragment) {
                    findNavController().popBackStack(R.id.myChinaplasLoginFragment, true)
                } else {
                    findNavController().navigate(R.id.myChinaplasLoginFragment)
                }
                LogUtil.i("logout::: findLoginFragment=$findLoginFragment")
            })
    }

    private fun showD5() {
        val adHelper = ADHelper.getInstance(requireActivity().application)
        val property = adHelper.d5Property(D5_MYCHINAPLAS)
        if (property.pageID.isEmpty() || !adHelper.isD5Open()) {
            ivD5.visibility = View.GONE
            return
        }
        val params = ConstraintLayout.LayoutParams(getScreenWidth(), adHelper.getADHeight())
        params.bottomToBottom = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        params.topToBottom = R.id.tv_logout
        ivD5.layoutParams = params

        val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
        Glide.with(this).load(adHelper.d5ImageUrl(D5_GENERATION)).apply(options).into(ivD5)

        ivD5.setOnClickListener {
            when (property.function) {
                1 -> findNavController().navigate(MyChinaplasFragmentDirections.actionToExhibitorDetailFragment(property.pageID))
                2 -> { // 同期活动
                    setItemEventID(property.pageID)
                    findNavController().navigate(R.id.eventDetailFragment)
                }
            }
        }
    }


}
