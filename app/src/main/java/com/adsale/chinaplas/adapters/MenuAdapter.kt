package com.adsale.chinaplas.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.MainIcon
import com.adsale.chinaplas.databinding.ItemMenuBinding
import com.adsale.chinaplas.utils.*
import kotlin.math.roundToInt

/**
 * Created by Carrie on 2019/10/16.
 */
class MenuAdapter(list: List<MainIcon>, private val clickListener: OnClickListener) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    var data = listOf<MainIcon>()
    private var menuHeight: Int = 0
    private var menuWidth: Int = 0
    private var mScreenWidth: Int = 0
    private var params: ConstraintLayout.LayoutParams? = null


    init {
        data = list
        mScreenWidth = getScreenWidth()
        LogUtil.i("mScreenWidth=$mScreenWidth, height=${getScreenHeight()}")
        if (isTablet()) {
            initPadSize()
        } else {
            initPhoneSize()
        }
        LogUtil.i("${data.size}")
    }

    private fun initPhoneSize() {
        val h1 = (getMainMenuHeight() - dp2px(2f)) / 2
        val h2 = (mScreenWidth * MAIN_MENU_HEIGHT) / MAIN_MENU_WIDTH / 3
        LogUtil.i("h1=$h1,h2=$h2")
        menuHeight = if (h1 > h2) h2 else h1
        menuWidth = (mScreenWidth / 3) - dp2px(1.5f)
        params = ConstraintLayout.LayoutParams(menuWidth, menuHeight)
        LogUtil.i("menuHeight=$menuHeight,menuWidth=$menuWidth")
    }

    /**
     * 平板 Menu 总宽度占 0.36   （设计图尺寸：one icon 358*315   2048px）
     */
    private fun initPadSize() {
        menuWidth = ((mScreenWidth * 0.36) / 2).toInt() - 1
//        menuHeight = (menuWidth * 315) / 358
        menuHeight = ((DESIGN_MAIN_BANNER_HEIGHT_PAD * getPadHeightRate()) / 3).toInt()
        params = ConstraintLayout.LayoutParams(menuWidth, menuHeight)
//        params!!.startToEnd = R.id.viewPager
//        params!!.marginStart = 2
        LogUtil.i("menuHeight=$menuHeight,menuWidth=$menuWidth")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder.from(parent, params!!)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.setOnClickListener {
            clickListener.onClick(item)
        }
        holder.bind(item)
    }

    class OnClickListener(val clickListener: (mainIcon: MainIcon) -> Unit) {
        fun onClick(mainIcon: MainIcon) = clickListener(mainIcon)
    }

    class MenuViewHolder private constructor(val binding: ItemMenuBinding, val params: ConstraintLayout.LayoutParams) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menu: MainIcon) {
            binding.obj = menu
            binding.menuCard.layoutParams = params
        }

        companion object {
            fun from(parent: ViewGroup, params: ConstraintLayout.LayoutParams): MenuViewHolder {
                val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return MenuViewHolder(binding, params)
            }
        }

    }


}