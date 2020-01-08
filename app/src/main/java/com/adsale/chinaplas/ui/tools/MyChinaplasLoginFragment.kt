package com.adsale.chinaplas.ui.tools


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.FragmentMyChinaplasLoginBinding
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.putMyChinaplasLogin
import com.adsale.chinaplas.utils.setMyChinaplasEmail
import com.adsale.chinaplas.utils.setMyChinaplasPhone
import com.adsale.chinaplas.viewmodels.MyChinaplasViewModel

/**
 * A simple [Fragment] subclass.
 */
class MyChinaplasLoginFragment : Fragment() {
    private lateinit var viewPager: ViewPager
    private lateinit var binding: FragmentMyChinaplasLoginBinding

    val viewModel by lazy {
        ViewModelProviders.of(this).get(MyChinaplasViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMyChinaplasLoginBinding.inflate(inflater)
        viewPager = binding.viewPagerLogin
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val fragments = mutableListOf<Fragment>()
        fragments.add(LoginPasswordFragment(viewModel))
        fragments.add(LoginSMSFragment(viewModel))
        LogUtil.i("fragments=${fragments}")

        viewPager.offscreenPageLimit = 1
        val adapter = LoginAdapter(childFragmentManager, fragments)
        viewPager.adapter = adapter

        viewModel.barClick.observe(this, Observer {
            if (it == 1) {
                viewPager.currentItem = 0
            } else if (it == 2) {
                viewPager.currentItem = 1
            }
        })

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.barClick.value = position + 1
            }
        })

        val navController = NavHostFragment.findNavController(this)
        viewModel.toDestination.observe(this, Observer {
            LogUtil.i("toDestination:${viewModel.toDestination.value!!}")
            if (it) {
                LogUtil.i("登录成功")
                putMyChinaplasLogin(true)
                setMyChinaplasEmail(viewModel.email.value!!)
                setMyChinaplasPhone(viewModel.phoneNo.value!!)
                viewModel.resetSubmitStatus()
                // todo 如何在跳转后，不返回登录页
                navController.popBackStack()
                findNavController().navigate(R.id.myChinaplasFragment)
//                navController
//                    .navigate(MyChinaplasLoginFragmentDirections.actionMyChinaplasLoginFragmentToMyChinaplasFragment())
//                navController.popBackStack(R.id.action_myChinaplasLoginFragment_to_myChinaplasFragment,true)

            }
        })
        viewModel.progressBarVisible.observe(this, Observer {
            if (it) {
                hideInput()
            }
        })


    }

    private fun getSubmitStatus() {}


    class LoginAdapter constructor(fm: FragmentManager, private val fragments: List<Fragment>) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return 2
        }


    }

    private fun hideInput() {
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }


}
