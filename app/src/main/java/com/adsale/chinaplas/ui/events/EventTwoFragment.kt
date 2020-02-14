package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.utils.DATE_2


/**
 * A simple [Fragment] subclass.
 */
class EventTwoFragment : BaseEventFragment() {

    override fun initedData() {
        itemClickListener = OnItemClickListener { entity, pos ->
            NavHostFragment.findNavController(this).navigate(
                EventTwoFragmentDirections.actionEventTwoFragmentToWebContentFragment(
                    "PlasticsRecycling",
                    getString(R.string.title_concurrent_event)
                )
            )
        }
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_2%")
    }

    companion object {
        @Volatile
        private var instance: EventTwoFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventTwoFragment().also { instance = it }
            }
    }


}
