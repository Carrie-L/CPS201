package com.adsale.chinaplas.ui.tools

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adsale.chinaplas.R
import com.adsale.chinaplas.viewmodels.MyScheduleViewModel


class MyScheduleFragment : Fragment() {

    companion object {
        fun newInstance() = MyScheduleFragment()
    }

    private lateinit var viewModel: MyScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.my_schedule_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyScheduleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
