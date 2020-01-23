package com.adsale.chinaplas.ui.exhibitors


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.adsale.chinaplas.R
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.ExhibitorDtlViewModel


/**
 * A simple [Fragment] subclass.
 */
class DtlInfoFragment constructor(private val viewModel: ExhibitorDtlViewModel) : Fragment() {
    private var isInited = false
    private var lastView: View? = null
    private lateinit var tvDesc: TextView
    private lateinit var tvNoData: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        if (lastView == null) {
            val view = inflater.inflate(R.layout.fragment_dtl_info, container, false)
            tvDesc = view.findViewById(R.id.tv_description)
            tvNoData = view.findViewById(R.id.tv_no_data)
            lastView = view
//        }
        return lastView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.company.observe(this, Observer { entity ->
            LogUtil.i("--info 2 - ${entity.getDescription()}")

            val desc = entity.getDescription()
            if (TextUtils.isEmpty(desc)) {
                tvNoData.visibility = View.VISIBLE
            } else {
                tvDesc.text = desc
            }

        })

    }


}
