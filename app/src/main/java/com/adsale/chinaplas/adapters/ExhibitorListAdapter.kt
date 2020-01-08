package com.adsale.chinaplas.adapters

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.adsale.chinaplas.BR
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.databinding.ItemExhibitorHeaderBinding
import com.adsale.chinaplas.databinding.ItemExhibitorListBinding
import com.adsale.chinaplas.utils.TYPE_AD
import com.adsale.chinaplas.utils.TYPE_HEADER
import com.adsale.chinaplas.utils.TYPE_SUB
import com.adsale.chinaplas.viewmodels.ExhibitorViewModel

/**
 * Created by Carrie on 2020/1/3.
 */
//class ExhibitorListAdapter(private var list: MutableList<Exhibitor>,
//                           listener: OnItemClickListener,
//                           private val viewModel: ExhibitorViewModel) :
//    CpsBaseAdapter<Exhibitor>(list, listener) {
//    private var entity: Exhibitor = Exhibitor()
//
//    override fun getLayoutIdForPosition(position: Int): Int {
//        entity = list[position]
//        if (entity.isD3Banner.get()) {
//            entity.isTypeLabel.set(TYPE_AD)
//            return R.layout.item_d3_banner
//        }
//        when {
//            position == 0 -> {
//                entity.isTypeLabel.set(TYPE_HEADER)
////                R.layout.item_exhibitor_header
//            }
//            list[position].getSort() == list[position - 1].getSort() -> {
//                entity.isTypeLabel.set(TYPE_SUB)
////                R.layout.item_exhibitor_list
//            }
//            else -> {
//                entity.isTypeLabel.set(TYPE_HEADER)
////                R.layout.item_exhibitor_header
//            }
//        }
//        return R.layout.item_exhibitor_list
//    }
//
//    override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
//        if (entity.isTypeLabel.get() != TYPE_AD) {
//            holder.binding.setVariable(BR.viewModel, viewModel)
//            holder.binding.setVariable(BR.pos, position)
//        }
//        super.onBindViewHolder(holder, position)
////        entity = list[position]
////        if (entity.isTypeLabel.get() == TYPE_HEADER) {
////            (holder.binding as ItemExhibitorHeaderBinding).ivStar.setOnClickListener {
////                onStar(entity, position)
////            }
////        } else if (entity.isTypeLabel.get() == TYPE_SUB) {
////            (holder.binding as ItemExhibitorListBinding).ivStar.setOnClickListener {
////                onStar(entity, position)
////            }
////        }
//    }
//
//    private fun onStar(entity: Exhibitor, position: Int) {
//        entity.IsFavourite = if (entity.IsFavourite == 0) 1 else 0
//        list[position] = entity
//        notifyItemChanged(position)
//    }
//
//    override fun setList(newList: List<Exhibitor>) {
//        this.list = newList as MutableList<Exhibitor>
//        super.setList(newList)
//    }
//}


class ExhibitorListAdapter(
    listener: OnItemClickListener,
    diffCallback: DiffUtil.ItemCallback<Exhibitor>, private val viewModel: ExhibitorViewModel) :
    CpsBaseDiffAdapter<Exhibitor>(listener, diffCallback) {
    private var entity: Exhibitor = Exhibitor()

    override fun onBindViewHolder(holder: CpsBaseDiffViewHolder, position: Int) {
        if (getItem(position).isTypeLabel.get() != TYPE_AD) {
            holder.binding.setVariable(BR.viewModel, viewModel)
            holder.binding.setVariable(BR.pos, position)
        }
        super.onBindViewHolder(holder, position)
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        entity = getItem(position)
        if (entity.isD3Banner.get()) {
            return R.layout.item_d3_banner
        }
        when {
            position == 0 -> {
                entity.isTypeLabel.set(TYPE_HEADER)
            }
            entity.getSort() == getItem(position - 1).getSort() -> {
                entity.isTypeLabel.set(TYPE_SUB)
            }
            else -> {
                entity.isTypeLabel.set(TYPE_HEADER)
            }
        }
        return R.layout.item_exhibitor_list
    }

}