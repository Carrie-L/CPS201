package com.adsale.chinaplas.ui.events


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.dao.ScheduleInfo
import com.adsale.chinaplas.ui.view.HELP_EVENT_DTL
import com.adsale.chinaplas.ui.view.HelpDialog
import com.adsale.chinaplas.utils.getHtmlPath
import com.adsale.chinaplas.utils.inTransaction
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * setItemEventID()
 */
class EventDetailFragment : Fragment() {
    private lateinit var webView: WebView
    private lateinit var mainViewModel: MainViewModel
    private var eventID: String = ""
    private lateinit var fabAdd: FloatingActionButton
    private var title: String = ""
    private lateinit var ivHelp: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_detail, container, false)
        webView = view.findViewById(R.id.event_web_view)
        fabAdd = view.findViewById(R.id.fab_add_schedule)
        ivHelp = view.findViewById(R.id.iv_help)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)

        arguments?.let {
            EventDetailFragmentArgs.fromBundle(it).eventID.let { id ->
                eventID = id
            }
            EventDetailFragmentArgs.fromBundle(it).title.let { title ->
                this.title = title
                mainViewModel.title.value = title
            }
        }

//        val eventID = getItemEventID()

        val path = getHtmlPath("ConcurrentEvent", eventID)
        webView.loadUrl(path)

        addSchedule()


        helpPage()
    }

    private fun addSchedule() {
        fabAdd.setOnClickListener {
            val scheduleInfo = ScheduleInfo(null, title, "", "", eventID, "", "", 2, 0, null)
            findNavController().navigate(EventDetailFragmentDirections.actionToScheduleEditFragment(true, scheduleInfo))
        }
    }

    private fun helpPage() {
        ivHelp.setOnClickListener {
            val helpDialog = HelpDialog.getInstance(HELP_EVENT_DTL)
            requireActivity().supportFragmentManager.inTransaction {
                helpDialog.show(fragmentManager!!, "Help")
            }
        }
    }

}
