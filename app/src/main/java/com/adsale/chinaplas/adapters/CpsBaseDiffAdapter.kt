package com.adsale.chinaplas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.BR

abstract class CpsBaseDiffAdapter<T>(
                                     private val onItemClickListener: OnItemClickListener,
                                     diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, CpsBaseDiffAdapter.CpsBaseDiffViewHolder>(diffCallback) {
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