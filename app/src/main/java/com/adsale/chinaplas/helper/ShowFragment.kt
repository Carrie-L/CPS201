package com.adsale.chinaplas.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Created by Carrie on 2019/12/11.
 */
//class ShowFragment {
//    inline fun <reified T : Fragment> FragmentActivity.showFragment(replaceViewId: Int): T {
//        val sfm = supportFragmentManager
//        val transaction = sfm.beginTransaction()
//    }

    inline fun <reified T : Fragment> FragmentActivity.showFragment(
        replaceViewId: Int, init: (T).() -> Unit = {}): T {
        val sfm = supportFragmentManager
        val transaction = sfm.beginTransaction()
        var fragment = sfm.findFragmentByTag(T::class.java.name)
        if (fragment == null) {
            fragment = T::class.java.newInstance()
            transaction.add(replaceViewId, fragment, T::class.java.name)
        }
        sfm.fragments.filter { it != fragment }.forEach { transaction.hide(it) }
        transaction.show(fragment)
        transaction.commitAllowingStateLoss()
        sfm.executePendingTransactions()
        init(fragment as T)
        return fragment
    }

    inline fun <reified T : Fragment> FragmentActivity.getFragment(
        init: (T)?.() -> Unit = {}): T? {
        val fragment = supportFragmentManager.findFragmentByTag(T::class.java.name)
        init(fragment as T?)
        return fragment
    }
//}
