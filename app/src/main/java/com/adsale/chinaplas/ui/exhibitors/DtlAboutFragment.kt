package com.adsale.chinaplas.ui.exhibitors


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adsale.chinaplas.R
import com.adsale.chinaplas.viewmodels.ExhibitorDtlViewModel


/**
 * A simple [Fragment] subclass.
 */
class DtlAboutFragment  constructor(private val viewModel: ExhibitorDtlViewModel): Fragment() {

    private var isInited = false
    private var lastView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        if (lastView == null) {
            val view = inflater.inflate(R.layout.fragment_dtl_about, container, false)
//            tvDesc = view.findViewById(R.id.tv_description)
            lastView = view
//        }
        return lastView
    }


}
