package com.adsale.chinaplas.ui.newtech

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.adsale.chinaplas.adapters.FilterAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.entity.ExhibitorFilter
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.databinding.ViewExhibitorFilterBinding
import com.adsale.chinaplas.utils.LogUtil

/**
 * Created by Carrie on 2020/1/8.
 */
class NewtechFilterView : LinearLayout {
    var filters = mutableListOf<ExhibitorFilter>()
    val itemName = ObservableField<String>()
    private lateinit var binding: ViewExhibitorFilterBinding
    private var adapter: FilterAdapter? = null

    private lateinit var viewModel: NewtechFilterViewModel
    private lateinit var recyclerView: RecyclerView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ViewExhibitorFilterBinding.inflate(inflater, this, true)

        recyclerView = binding.filterRecyclerView
        val gridLayoutManager = GridLayoutManager(context, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect,
                                        view: View,
                                        parent: RecyclerView,
                                        state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right = 8
                outRect.left = 8
                outRect.bottom = 8
            }
        })
        adapter = FilterAdapter(mutableListOf(), itemClearListener)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.GONE
    }

    /**
     * KV:  K - index， V： name
     */
    fun setData(viewModel: NewtechFilterViewModel, entity: KV) {
        this.viewModel = viewModel
        binding.obj = entity
        binding.executePendingBindings()
    }

    fun setList(list: List<ExhibitorFilter>) {
        LogUtil.i("setList: ${list.size}")
        if (list == filters) {
            LogUtil.i("list==filters")
        } else {
            filters.clear()
            filters.addAll(list)
            adapter?.setList(filters)
        }
        recyclerView.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private val itemClearListener = OnItemClickListener { entity, _ ->
        entity as ExhibitorFilter
        LogUtil.i("onClear:=" + entity.filter)
        val size = filters.size
        for (i in 0 until size) {
            if (entity.id.equals(filters[i].id)) {
                filters.removeAt(i)
                when (entity.index) {
//                    FILTER_INDEX_INDUSTRY -> viewModel.industryFilters.value = filters
//                    FILTER_INDEX_APPLICATION -> viewModel.appFilters.value = filters
//                    FILTER_INDEX_THEME -> viewModel.regionFilters.value = filters
//                    FILTER_INDEX_NEW -> viewModel.hallFilters.value = filters
                }
                recyclerView.visibility = if (filters.isNotEmpty()) View.VISIBLE else View.GONE
                adapter?.setList(filters)
                break
            }
        }
    }

}