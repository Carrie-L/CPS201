package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.utils.DATE_3

/**
 * A simple [Fragment] subclass.
 */
class EventThreeFragment : BaseEventFragment() {

    override fun initedData() {
        layoutId = R.layout.item_event_part
        itemClickListener = OnItemClickListener { entity, pos ->
            NavHostFragment.findNavController(this).navigate(
                EventThreeFragmentDirections.actionEventThreeFragmentToWebContentFragment(
                    "PlasticsRecycling",
                    getString(R.string.title_concurrent_event)
                )
            )
        }
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_3%")
    }

    companion object {
        @Volatile
        private var instance: EventThreeFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventThreeFragment().also { instance = it }
            }
    }

}
