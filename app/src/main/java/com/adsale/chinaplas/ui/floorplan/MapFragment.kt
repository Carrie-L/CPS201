package com.adsale.chinaplas.ui.floorplan


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.adsale.chinaplas.R
import java.math.BigDecimal
import android.app.Activity
import android.widget.Toast
import org.json.JSONException
import com.adsale.chinaplas.utils.LogUtil
import com.paypal.android.sdk.payments.*
import com.tencent.bugly.Bugly.applicationContext
import java.util.*
import kotlin.collections.HashSet
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
open class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}
