package com.adsale.chinaplas.ui.tools


import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment

import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.FragmentToolBinding
import com.adsale.chinaplas.utils.isMyChinaplasLogin

/**
 * A simple [Fragment] subclass.
 */
class ToolFragment : Fragment() {

    private lateinit var binding: FragmentToolBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolBinding.inflate(inflater)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val navController = NavHostFragment.findNavController(this)
        binding.tvMyCps.setOnClickListener {
            if (isMyChinaplasLogin()) {
                navController.navigate(ToolFragmentDirections.actionMenuToolToMyChinaplasFragment())
            } else {
                navController.navigate(ToolFragmentDirections.actionMenuToolToMyChinaplasLoginFragment())
            }
        }




    }


}
