package com.adsale.chinaplas.ui.drawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.R
import com.adsale.chinaplas.viewmodels.FollowViewModel

class FollowFragment : Fragment() {

    private lateinit var slideshowViewModel: FollowViewModel
//    private lateinit var mainViewModel: MainViewModel




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProviders.of(this).get(FollowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        slideshowViewModel.text.observe(this, Observer {
            textView.text = it
        })



//        mainViewModel = ViewModelProviders.of(this, MainViewModelFactory(requireNotNull(this.activity).application))
//            .get(MainViewModel::class.java)

        return root
    }
}