package com.adsale.chinaplas.ui.exhibitors


import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders

import com.adsale.chinaplas.R
import com.adsale.chinaplas.cpsDatabase
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.databinding.FragmentRegisterFormBinding
import com.adsale.chinaplas.viewmodels.RegPickViewModel
import com.adsale.chinaplas.viewmodels.RegViewModelFactory
import com.adsale.chinaplas.viewmodels.RegisterViewModel

/**
 *
 */
class ExhibitorFilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterFormBinding.inflate(inflater)


        return binding.root


    }


}
