package com.adsale.chinaplas.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.BR

abstract class CpsBaseDiffAdapter<T>(
    private val onItemClickListener: OnItemClickListener) :
    ListAdapter<T, CpsBaseDiffAdapter.CpsBaseDiffViewHolder>(DiffCallback()) {
    private lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CpsBaseDiffViewHolder {
        val holder = CpsBaseDiffViewHolder.from(parent, viewType)
        binding = holder.binding
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    override fun onBindViewHolder(holder: CpsBaseDiffViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(item, position)
        }
        holder.bind(item)
        bindVariable(binding)
    }

    class CpsBaseDiffViewHolder private constructor(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(obj: Any?) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewType: Int): CpsBaseDiffViewHolder {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),
                    viewType,
                    parent,
                    false);
                return CpsBaseDiffViewHolder(binding)
            }
        }
    }

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    open fun bindVariable(binding: ViewDataBinding) {
        binding.executePendingBindings()
    }

}

private class DiffCallback<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

