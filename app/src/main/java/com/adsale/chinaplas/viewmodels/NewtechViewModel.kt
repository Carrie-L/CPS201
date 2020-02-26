package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.NewtechProductInfo
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.utils.LogUtil
import kotlinx.coroutines.*
import java.util.*


const val NEWTECH_CLICK_FILTER = 1
const val NEWTECH_CLICK_RESET = 2
const val NEWTECH_CLICK_HELP = 3


class NewtechViewModel(val newtechRepository: NewtechRepository) : ViewModel() {

    //  1  筛选中； 2 重置; 3 帮助页
    var clickState = MutableLiveData<Int>(0)

    //    var list=MutableLiveData<List<NewTech>>
    private val products = mutableListOf<NewtechProductInfo>()  /*  没有广告的、从数据库取出的 产品列表, 用于 搜索 时判断 */
    private val productCaches = mutableListOf<NewtechProductInfo>() /*  没有广告的、从数据库取出的 产品列表缓存，当返回筛选结果时，在这个列表里选择符合条件的数据 */
    private val listCaches = mutableListOf<NewtechProductInfo>()  /* 包含产品和广告的全部列表缓存，用于清空搜索时还原列表 */
    val listCaches0 = mutableListOf<NewtechProductInfo>()  /* 包含产品和广告的全部列表缓存. 一開始的全部列表 */
    private val searchList = mutableListOf<NewtechProductInfo>()
    private val filterList = mutableListOf<NewtechProductInfo>()
    var list = MutableLiveData<MutableList<NewtechProductInfo>>()  /*  交给adapter的列表（可能有产品和广告） */

    private
    val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

    fun onClick(index: Int) {
        clickState.value = index
    }


    fun reset() {
        list.value = listCaches0
    }

    fun search(text: String) {
        LogUtil.i("queryExhibitorsLocal : $text")
        synchronized(list) {
            LogUtil.i("queryExhibitorsLocal 1 : $text")
            list.value = findSearchFromLocal(text)
        }

    }

    private fun findSearchFromLocal(text: String): MutableList<NewtechProductInfo> {
        searchList.clear()
        val size = listCaches0.size
        var entity: NewtechProductInfo
        for (i in 0 until size) {
            entity = listCaches0[i]
            if (entity.BoothNo.toLowerCase(Locale.ENGLISH).contains(text)
                || entity.isContainsCompany(text)
                || entity.isContainsProductName(text)) {
                searchList.add(entity)
            }
        }
        return searchList
    }

    fun getAllProductInfoList(isList: Boolean) {
        uiScope.launch {
            listCaches0.clear()
            listCaches0.addAll(getAllProductInfoListFromDB())
            if (isList) {
                list.value = listCaches0
            }
            LogUtil.i("list = ${list.value?.size},  ${list.toString()}")
        }
    }

    fun getFilterList(filterSql: String) {
        uiScope.launch {
            filterList.clear()
            filterList.addAll(getFilterListFromDB(filterSql))
            list.value = filterList
            LogUtil.i("list = ${list.value?.size},  ${list.toString()}")
        }
    }

    fun insertAdList() {

    }


    private suspend fun getAllProductInfoListFromDB(): MutableList<NewtechProductInfo> {
        return withContext(Dispatchers.IO) {
            newtechRepository.getAllProductInfoList()
        }
    }

    private suspend fun getFilterListFromDB(filterSql: String): MutableList<NewtechProductInfo> {
        return withContext(Dispatchers.IO) {
            newtechRepository.getFilterResultList(filterSql)
        }
    }

    /*  ※※※※※※※※ 详情页    ※※※※※※※※※※ */
    var itemInfo = MutableLiveData<NewtechProductInfo>()

    fun getItemInfo(rid: String) {
        uiScope.launch {
            itemInfo.value = getItemFromDB(rid)
        }
    }

    private suspend fun getItemFromDB(rid: String): NewtechProductInfo {
        return withContext(Dispatchers.IO) {
            newtechRepository.getItemInfo(rid)
        }
    }

    var imageClick = MutableLiveData("")
    var boothClick = MutableLiveData("")

    fun onImageClick(image: String) {
        LogUtil.i("  onImageClick= $image ")
        imageClick.value = image
    }

    fun onBoothClick(booth: String) {
        LogUtil.i("  onBoothClick= $booth ")
        boothClick.value = booth
    }


    fun resetClickState() {
        clickState.value = 0
    }


}