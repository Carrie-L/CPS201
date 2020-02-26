package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import com.adsale.chinaplas.utils.DATE_4


/**
 * A simple [Fragment] subclass.
 */
class EventFourFragment : BaseEventFragment() {

    override fun initedData() {
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_4%")
    }



}
