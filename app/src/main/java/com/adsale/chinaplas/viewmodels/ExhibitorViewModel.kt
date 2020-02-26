package com.adsale.chinaplas.viewmodels

import android.app.Application
import android.os.CountDownTimer
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.*
import java.util.*

/**
 * Created by Carrie on 2020/1/3.
 */
class ExhibitorViewModel(
    val app: Application,
    private val exhibitorRepository: ExhibitorRepository
) :
    AndroidViewModel(app) {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

    private var _exhibitorSZCaches: MutableList<Exhibitor> = mutableListOf()  /* sz 全部参展商列表 */
    private var _exhibitorSearchCaches: MutableList<Exhibitor> = mutableListOf()  /* sz 全部参展商列表 */
    private var _exhibitorHalls: MutableList<Exhibitor> = mutableListOf()  /*  hall list. 用于排序 */

    private var _exhibitors = MutableLiveData<MutableList<Exhibitor>>()  /* adapter list */
    //    private lateinit var _exhibitors: LiveData<List<Exhibitor>> =   /* adapter list */
    //    val exhibitors: LiveData<MutableList<Exhibitor>>
    val exhibitors: MutableLiveData<MutableList<Exhibitor>>
        get() = _exhibitors
//                as MutableLiveData<List<Exhibitor>>

    private var sideTemps = mutableListOf<String>()

    var entity: Exhibitor = Exhibitor()
    var isSortBySZ = ObservableBoolean(true)   // true: sz, false: hall

    private var _navigateFilter = MutableLiveData<Boolean>(false)
    val navigateFilter: MutableLiveData<Boolean>
        get() = _navigateFilter

    var searchText = MutableLiveData<String>()
//    var itemEntity = MutableLiveData<Exhibitor>()

    private var _isItemUpdate = MutableLiveData<KV>(KV(-1, false))
    val isItemUpdate: MutableLiveData<KV>
        get() = _isItemUpdate

    var itemPos = MutableLiveData(-1)
    var itemHasUpdate = MutableLiveData(false)
    var starDialog = MutableLiveData(false)

    init {
        adCountDownTime()
    }

    fun getExhibitorList(type: String, value: String) {
        when (type) {
            EXHIBITOR_FILTER -> getFilterExhibitors(value)
            EXHIBITOR_INDUSTRY -> getIndustryInExhibitors(value)
            EXHIBITOR_APPLICATION -> getApplicationInExhibitors(value)
            else -> getAllExhibitors()
        }
    }

    private fun getFilterExhibitors(filterSql: String) {
        uiScope.launch {
            _exhibitorSZCaches = getFilterExhibitorsFromDB(filterSql)
            _exhibitors.value = _exhibitorSZCaches
            i("getFilterExhibitors: ${_exhibitors.value?.size}")
        }
    }

    private fun getIndustryInExhibitors(id: String) {
        uiScope.launch {
            _exhibitorSZCaches = getIndustryExhibitorsFromDB(id)
            _exhibitors.value = _exhibitorSZCaches
            i("getIndustryInExhibitors: ${_exhibitors.value?.size}")
        }
    }

    private fun getApplicationInExhibitors(id: String) {
        uiScope.launch {
            _exhibitorSZCaches = getApplicationExhibitorsFromDB(id)
            _exhibitors.value = _exhibitorSZCaches
            i("getApplicationInExhibitors: ${_exhibitors.value?.size}")
        }
    }

    private fun getAllExhibitors() {
        uiScope.launch {
            _exhibitorSZCaches = getAllExhibitorsSZ()
            _exhibitors.value = _exhibitorSZCaches
            i("_exhibitors getAllExhibitors : ${_exhibitors.value?.size}, ${exhibitors.value.toString()}")
        }
    }

    private suspend fun getAllExhibitorsSZ(): MutableList<Exhibitor> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getAllExhibitorList()
        }
    }

    private suspend fun getFilterExhibitorsFromDB(filterSql: String): MutableList<Exhibitor> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.filterExhibitors(filterSql)
        }
    }

    private suspend fun getIndustryExhibitorsFromDB(id: String): MutableList<Exhibitor> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getIndustryInExhibitors(id)
        }
    }

    private suspend fun getApplicationExhibitorsFromDB(id: String): MutableList<Exhibitor> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getApplicationInExhibitors(id)
        }
    }

    suspend fun getItemCompanyFromDB(companyId: String): Exhibitor {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getCompanyItem(companyId)
        }
    }

    fun getItemCompany() {
        uiScope.launch {
            val pos = itemPos.value!!
            var entity = _exhibitors.value!![pos]
            entity = getItemCompanyFromDB(entity.CompanyID)
            val list = _exhibitors.value
            list!![pos] = entity
            _exhibitors.value = list
        }
    }


//    private suspend fun queryExhibitorsFromLocal(text: String): MutableList<Exhibitor> {
//        return withContext(Dispatchers.IO) {
//            val size = _exhibitorSZCaches.size
//            _exhibitorSearchCaches.clear()
//            for (i in 0 until size) {
//                entity = _exhibitorSZCaches[i]
//                if (entity.CompanyNameCN!!.toLowerCase(Locale.getDefault()).contains(text)
//                    || entity.CompanyNameTW!!.toLowerCase(Locale.getDefault()).contains(text)
//                    || entity.CompanyNameEN!!.toLowerCase(Locale.getDefault()).contains(text)
//                    || entity.BoothNo!!.toLowerCase(Locale.getDefault()).contains(text)) {
//                    _exhibitorSearchCaches.add(entity)
////                    lettersAZ.add(exhibitor.getSort())
//                }
//            }
//            _exhibitorSearchCaches
//        }
//    }

    private fun queryExhibitorsFromLocal(text: String): MutableList<Exhibitor> {
        val size = _exhibitorSZCaches.size
        _exhibitorSearchCaches.clear()
        for (i in 0 until size) {
            entity = _exhibitorSZCaches[i]
            if (entity.CompanyNameCN!!.toLowerCase(Locale.getDefault()).contains(text)
                || entity.CompanyNameTW!!.toLowerCase(Locale.getDefault()).contains(text)
                || entity.CompanyNameEN!!.toLowerCase(Locale.getDefault()).contains(text)
                || entity.BoothNo!!.toLowerCase(Locale.getDefault()).contains(text)
            ) {
                _exhibitorSearchCaches.add(entity)
//                    lettersAZ.add(exhibitor.getSort())
            }
        }
        return _exhibitorSearchCaches
    }

    fun queryExhibitorsLocal(text: String) {
        i("queryExhibitorsLocal : $text")
        synchronized(_exhibitors) {
            i("queryExhibitorsLocal 1 : $text")
            //            uiScope.launch {
            val startTime = System.currentTimeMillis()
            _exhibitors.value = queryExhibitorsFromLocal(text)
            val endTime = System.currentTimeMillis()
            i("queryExhibitorsLocal spend time: ${endTime - startTime} ms")
            i("_exhibitors:  queryExhibitorsLocal :  ${_exhibitors.value?.size}}")
        }
//        }
    }

    fun resetList() {
        isSortBySZ.set(true)
        _exhibitorSearchCaches.clear()
        _exhibitors.value = _exhibitorSZCaches
    }

    fun onStar(entity: Exhibitor, pos: Int) {
        if (isMyChinaplasLogin()) {
            entity.IsFavourite = if (entity.IsFavourite == 0) 1 else 0
            i("onStar: IsFavourite=${entity.IsFavourite}, pos=${pos}, name = ${entity.getCompanyName()}")
            entity.isStared.set(!entity.isStared.get())
            _exhibitors.value!![pos] = entity
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    exhibitorRepository.updateExhibitorItem(entity)
                }
            }
        } else {
            starDialog.value = true
        }
    }

    /**
     * 按拼音排序
     */
    fun onSortAZ() {
        if (!isSortBySZ.get()) {
            isSortBySZ.set(true)
            i("按拼音排序")
            if (_exhibitorSearchCaches.isEmpty()) {
                _exhibitors.value = _exhibitorSZCaches
            } else {
                _exhibitors.value = _exhibitorSearchCaches
            }
        }
    }

    /**
     * 按展馆排序
     */
    fun onSortHall() {
        if (isSortBySZ.get()) {
            isSortBySZ.set(false)
            i("按展馆排序")
            _exhibitorHalls.clear()
            _exhibitorHalls.addAll(_exhibitors.value!!)
            _exhibitorHalls.sortBy { it.seqHall }
            _exhibitors.value = _exhibitorHalls
            i("onSortHall: _exhibitors=${_exhibitors.value.toString()}")
        }

    }

    fun navigateFilter() {
        _navigateFilter.value = true
    }

    fun finishNavigateFilter() {
        _navigateFilter.value = false
    }

    fun setItemHasUpdate(pos: Int) {
//        _isItemUpdate.value = KV(pos, true)
        itemHasUpdate.value = true
    }

    fun resetItemUpdate() {
//        _isItemUpdate.value = KV(-1, false)
        itemHasUpdate.value = false
        itemPos.value = -1
    }

    fun setSideBarList(): List<String> {
        // 侧边bar ， 去重
        sideTemps.clear()
        for (entity in _exhibitors.value!!) {
            if (isSortBySZ.get()) {
                sideTemps.add(entity.getSort())
            } else {
                sideTemps.add(entity.HallNo!!)
            }
        }
        if (sideTemps.isNotEmpty()) {
            sideTemps = sideTemps.distinct() as MutableList<String>
        }
        if (!isSortBySZ.get()) {
            sideTemps.sort()
        }
        i("sideTemps 1st = ${sideTemps.size},  ${sideTemps.toString()}")
        return sideTemps
    }

    private lateinit var adTimer: CountDownTimer
    var adRollIndex = MutableLiveData(-1)
    var adIndex: Int = 0
    private fun adCountDownTime() {
        adTimer = object : CountDownTimer(2000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {   //  millisUntilFinished / ONE_SECOND = 4,3,2,1,0
            }

            override fun onFinish() {
                LogUtil.i("d3 adSize=$adSize")
                adIndex++
                if (adIndex == adSize) {
                    adIndex = 0
                }
                adRollIndex.value = adIndex
                i("*****d3 adIndex=$adIndex")

                adCountDownTime()  // 无限轮播
            }
        }
        adTimer.start()
    }

    private var adSize = 0
    fun setD3Size(size: Int) {
        adSize = size
    }

    fun startAdTimer() {
        if (adRollIndex.value == -1) {
            i("* startTimer *")
            adTimer.start()
//            adRollIndex.value = 0
        }
    }

    fun stopTimer() {
        adRollIndex.value = -1
        adTimer.cancel()
    }


}