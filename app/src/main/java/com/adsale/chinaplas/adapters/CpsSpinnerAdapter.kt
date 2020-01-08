package com.adsale.chinaplas.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.RegOptionData
import com.adsale.chinaplas.databinding.ItemRegTextBinding
import com.adsale.chinaplas.utils.LogUtil

/**
 * Created by Carrie on 2019/11/19.
 */
class RegSpinnerAdapter(private var list: List<RegOptionData>) : BaseAdapter() {

    fun setList(newList: List<RegOptionData>){
        list = newList
        notifyDataSetChanged()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemRegTextBinding.inflate(LayoutInflater.from(parent?.context))
        binding.obj = list[position]

//        LogUtil.i("getView :" + binding.obj)

//        binding.textView.setOnClickListener {
//            LogUtil.i("setOnClickListener :" + binding.obj)
//            onItemClickListener.onClick(binding.obj, position)
//        }

        return binding.root
    }


    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

//    override fun getLayoutIdForPosition(position: Int): Int {
//        return R.layout.item_reg_text
//    }

}