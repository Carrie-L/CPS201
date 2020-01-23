package com.adsale.chinaplas.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by Carrie on 2020/1/10.
 */
//class ExhibitorDtlAdapter(list: List<Fragment>, listener: OnItemClickListener) :
//    CpsBaseAdapter<Fragment>(list, listener) {
//    override fun getLayoutIdForPosition(position: Int): Int {
//        return R.layout.item_exh_dtl_fragment
//    }
//
//}

class ExhibitorDtlAdapter(manager: FragmentManager, private val list: List<Fragment>) : FragmentPagerAdapter(manager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: View, position: Int): Any {
        return super.instantiateItem(container, position)
    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        // super.destroyItem(container, position, object);
    }


}