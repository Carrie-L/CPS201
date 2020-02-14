package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.utils.DATE_1

/**
 * A simple [Fragment] subclass.
 */
class EventOneFragment : BaseEventFragment() {

    override fun initedData() {
        itemClickListener = OnItemClickListener { entity, pos ->
            NavHostFragment.findNavController(this).navigate(
                EventSeminarFragmentDirections.actionEventSeminarFragmentToWebContentFragment(
                    "PlasticsRecycling",
                    getString(R.string.title_concurrent_event)
                )
            )
        }
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_1%")
    }

    companion object {
        @Volatile
        private var instance: EventOneFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventOneFragment().also { instance = it }
            }
    }

}
