package com.adsale.chinaplas.ui.events


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.utils.DATE_1
import com.adsale.chinaplas.utils.DATE_4


/**
 * A simple [Fragment] subclass.
 */
class EventFourFragment : BaseEventFragment() {

    override fun initedData() {
        itemClickListener = OnItemClickListener { entity, pos ->
            NavHostFragment.findNavController(this).navigate(
                EventFourFragmentDirections.actionEventFourFragmentToWebContentFragment(
                    "PlasticsRecycling",
                    getString(R.string.title_concurrent_event)
                )
            )
        }
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_4%")
    }

    companion object {
        @Volatile
        private var instance: EventFourFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventFourFragment().also { instance = it }
            }
    }


}
