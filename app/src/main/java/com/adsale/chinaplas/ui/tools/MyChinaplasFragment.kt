package com.adsale.chinaplas.ui.tools


import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.MyChinaplasAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.entity.MyChinaplasEntity
import com.adsale.chinaplas.databinding.FragmentMyChinaplasBinding
import com.adsale.chinaplas.network.MY_CHINAPLAS_HOME_URL
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.adsale.chinaplas.viewmodels.MyChinaplasViewModel

/**
 * A simple [Fragment] subclass.
 * todo: 1. sync    2. 样式调整，item背景颜色,icon大小，logout图标   3. 获取发票Url
 * 预登记 -> guid，  登录 -> member id。
 * 因此预登记和登录是两个操作，登录状态不能一起。
 * 最好是，当没有guid时，隐藏[获取发票],而MyChinaplas 登录下，有一个跳转去预登记的链接
 *
 */
class MyChinaplasFragment : Fragment() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(
            requireActivity(),
            MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)
    }
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MyChinaplasViewModel::class.java)
    }


    private lateinit var binding: FragmentMyChinaplasBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMyChinaplasBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (TextUtils.isEmpty(getToken())) {
            viewModel.getTokenAsync()
        }

        val list = mutableListOf<MyChinaplasEntity>()

        val entity1 = MyChinaplasEntity("1", getString(R.string.cps_home), R.drawable.ic_cps_home)
        val entity2 = MyChinaplasEntity("2", getString(R.string.cps_my_info), R.drawable.ic_cps_my)
        val entity3 = MyChinaplasEntity("3", getString(R.string.cps_colleague_info), R.drawable.ic_cps_colleguage)
        val entity4 = MyChinaplasEntity("sync", getString(R.string.cps_sync_exhibitor), R.drawable.ic_cps_sync)
        val entity5 = MyChinaplasEntity("5", getString(R.string.cps_get_invoice), R.drawable.ic_cps_invoice)
        val entity6 = MyChinaplasEntity("logout", getString(R.string.logout), R.drawable.ic_test)
        list.add(entity1)
        list.add(entity2)
        list.add(entity3)
        list.add(entity4)
        list.add(entity5)
        list.add(entity6)

        val recyclerView = binding.rvMyCps
        val layoutManager = GridLayoutManager(context, getSpanCount())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val adapter = MyChinaplasAdapter(list, onItemClickListener)
        recyclerView.adapter = adapter

        back()
    }

    private val onItemClickListener = OnItemClickListener { entity, pos ->
        when ((entity as MyChinaplasEntity).PageID) {
            "sync" -> {
                // todo 我的参展商
            }
            "logout" -> logout()
            else -> toWebView()
        }
    }

    private fun toWebView() {
        val homeUrl = String.format(MY_CHINAPLAS_HOME_URL, getLangStr(), getToken())
        LogUtil.i("homeUrl=$homeUrl")
        NavHostFragment.findNavController(this)
            .navigate(MyChinaplasFragmentDirections.actionMyChinaplasFragmentToWebViewFragment(homeUrl,
                getString(R.string.title_my_chinaplas)))
    }

    private fun logout() {
        alertDialogTwoButton(context!!,
            R.string.logout_message,
            R.string.logout,
            R.string.cancel,
            DialogInterface.OnClickListener { dialog, which ->
                resetLoginInfo()
                NavHostFragment.findNavController(this)
                    .navigate(R.id.myChinaplasLoginFragment)
            })
    }

    private fun back() {
//        mainViewModel.backClicked.observe(this, Observer {
//            if (it) {
//                NavHostFragment.findNavController(this)
//                    .navigate(MyChinaplasFragmentDirections.actionMyChinaplasFragmentToMenuTool())
//            }
//        })
    }


}
