package com.adsale.chinaplas.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.ScheduleInfo
import com.baidu.speech.utils.LogUtil

/**
 * Created by Carrie on 2020/3/3.
 */
class ScheduleAdapter(private var list: List<List<ScheduleInfo>>, private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private lateinit var adapter: ScheduleItemAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        LogUtil.i("onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_recycler_view2, parent, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(view.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        adapter = ScheduleItemAdapter(listOf(), itemClickListener)
        recyclerView.adapter = adapter

        return ScheduleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        if (list.isNotEmpty()) {
            if (list[position].isEmpty()) {
                holder.isNoDateView.visibility = View.VISIBLE
            } else {
                holder.isNoDateView.visibility = View.GONE
                adapter.setList(list[position])
            }
        }
    }

    fun setList(newList: List<List<ScheduleInfo>>) {
        this.list = newList
        notifyDataSetChanged()
    }

    class ScheduleViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        val isNoDateView: TextView = view.findViewById(R.id.tv_no_data)
    }

    class ScheduleItemAdapter(list: List<ScheduleInfo>, itemClickListener: OnItemClickListener) :
        CpsBaseAdapter<ScheduleInfo>(list, itemClickListener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_schedule
        }
    }


}