package com.adsale.chinaplas.adapters

/**
 * Created by Carrie on 2019/11/15.
 */

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class CpsBaseListAdapter<T>(private val onItemClickListener: OnItemClickListener, private val diffCallback: DiffCallback<T>) : ListAdapter<T, CpsBaseAdapter.CpsBaseViewHolder>(diffCallback) {
    private lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CpsBaseAdapter.CpsBaseViewHolder {
        val holder = CpsBaseAdapter.CpsBaseViewHolder.from(parent, viewType)
        binding = holder.binding
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    override fun onBindViewHolder(holder: CpsBaseAdapter.CpsBaseViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(item, position)
        }
        holder.bind(item)
        bindVariable(binding)
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    open fun bindVariable(binding: ViewDataBinding) {
        binding.executePendingBindings()
    }

    open class DiffCallback<T> : DiffUtil.ItemCallback<T>() {

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }
    }

}

