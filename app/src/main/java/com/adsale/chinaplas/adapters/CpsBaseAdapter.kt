package com.adsale.chinaplas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.BR
import com.adsale.chinaplas.utils.LogUtil

abstract class CpsBaseAdapter<T>(private var list: List<T>, private val onItemClickListener: OnItemClickListener?) :
    RecyclerView.Adapter<CpsBaseAdapter.CpsBaseViewHolder>() {
    private lateinit var binding: ViewDataBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CpsBaseViewHolder {
        val holder = CpsBaseViewHolder.from(parent, viewType)
        binding = holder.binding
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    open fun setList(newList: List<T>) {
        list = newList
        notifyDataSetChanged()
    }

    open fun setListNoChange(newList: List<T>) {
        list = newList
    }

    override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
        val item = list[position]
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener {
                onItemClickListener.onClick(item, position)
            }
        }
        holder.bind(item)
        bindVariable(binding)
    }

    class CpsBaseViewHolder private constructor(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(obj: Any?) {
            binding.setVariable(BR.obj, obj)
            binding.executePendingBindings()
        }

        fun bindVariable() {
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, viewType: Int): CpsBaseViewHolder {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),
                    viewType,
                    parent,
                    false);
                return CpsBaseViewHolder(binding)
            }
        }
    }

    //    protected abstract fun getObjForPosition(position: Int): Any

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    open fun bindVariable(binding: ViewDataBinding) {
        binding.executePendingBindings()
    }


}

/**
 * Custom listener that handles clicks on [RecyclerView] items.  Passes the [MarsProperty]
 * associated with the current item to the [onClick] functionEntity.
 * @param clickListener lambda that will be called with the current [MarsProperty]
 */
open class OnItemClickListener(val clickListener: (entity: Any?, pos: Int) -> Unit) {
    fun onClick(item: Any?, position: Int) = clickListener(item, position)
}