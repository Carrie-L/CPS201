package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.SeminarInfo
import com.adsale.chinaplas.data.dao.SeminarRepository
import com.adsale.chinaplas.data.dao.SeminarSpeaker
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*

/**
 * Created by Carrie on 2020/2/14.
 */
const val SEMINAR_RESET = 4
const val SEMINAR_MAP = 5
const val SEMINAR_FILTER = 6

class SeminarViewModel(private val seminiarRepository: SeminarRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job
    /**
     * @第一天 1
     * @第二天 2
     * @第三天 3
     * @重置 4
     * @地图 5
     * @筛选 6
     */
    var btnClick = MutableLiveData(1)

    // 1:第一天，2 第二天, 3 第三天
    var currentDateSelect = MutableLiveData(1)
    var currentIsAm = MutableLiveData(true)

    /**
     * adapter用来显示的list. 包括从数据库中取出的 21/22/23号上午/下午list, 也包括筛选过后的某日上午list
     */
//    var seminarAmList = MutableLiveData<List<SeminarInfo>>()
//    var seminarPmList = MutableLiveData<List<SeminarInfo>>()
    var seminarList = MutableLiveData<List<SeminarInfo>>()
    var seminarAmList: MutableList<SeminarInfo> = mutableListOf()
    var seminarPmList: MutableList<SeminarInfo> = mutableListOf()
    var seminarFilterList: MutableList<SeminarInfo> = mutableListOf()   // 筛选结果列表
    var seminarAmListCaches: MutableList<SeminarInfo> = mutableListOf()  // 例如，保存的21号上午列表数据. 重置之后，原列表数据
    var seminarPmListCaches: MutableList<SeminarInfo> = mutableListOf()  // 例如，21号下午列表数据


    init {
        LogUtil.i("SeminarViewModel init~~~")
    }

    fun onFunctionClick(index: Int) {
        btnClick.value = index
    }

    fun onAmClick() {
        currentIsAm.value = true
        if (seminarAmListCaches.size == 0) {
            getSeminarTimeList(true)
        } else {
            seminarList.value = seminarAmList
        }
    }

    fun onPmClick() {
        currentIsAm.value = false
        if (seminarPmListCaches.size == 0) {
            getSeminarTimeList(false)
        } else {
            seminarList.value = seminarPmList
        }
    }

    fun getSeminarTimeList(isAm: Boolean) {
        uiScope.launch {
            if (isAm) {
                seminarAmList = getSeminarTimeListFromDB(isAm) as MutableList<SeminarInfo>
                seminarAmListCaches.addAll(seminarAmList)
                seminarList.value = seminarAmList
            } else {
                seminarPmListCaches.addAll(getSeminarTimeListFromDB(isAm))
                seminarPmList.addAll(seminarPmListCaches)
                seminarList.value = seminarPmList
            }
            LogUtil.i("seminarList=${seminarList.value?.size}")
        }
    }

    private suspend fun getSeminarTimeListFromDB(isAm: Boolean): List<SeminarInfo> {
        LogUtil.i("getCurrentDate=${getCurrentDate()}")
        return withContext(Dispatchers.IO) {
            val filterStr = getSPSeminarFilter()
            if (filterStr.isEmpty()) {
                seminiarRepository.getSeminarTimeList(
                    getLangCode(), getCurrentDate(), isAm)
            } else {
                seminiarRepository.getFilterSeminars(getLangCode(),
                    getCurrentDate(), isAm, filterStr)
            }
        }
    }

    private fun getCurrentDate(): String {
        LogUtil.i("getCurrentDate=${currentDateSelect.value}")
        return currentDateSelect.value!!.let {
            when (it) {
                2 -> "%$DATE_2%"
                3 -> "%$DATE_3%"
                else -> "%$DATE_1%"
            }
        }
    }

    /*技术交流会详情页*/
    var infoEntity = MutableLiveData<SeminarInfo>()
    var speakerEntity = MutableLiveData<SeminarSpeaker>()

    fun getDetailInfo(eventId: Int) {
        uiScope.launch {
            infoEntity.value = getInfoFromDB(eventId)
            speakerEntity.value = getSpeakFromDB(eventId)
        }
    }

    private suspend fun getInfoFromDB(eventId: Int): SeminarInfo {
        return withContext(Dispatchers.IO) {
            seminiarRepository.getSpeakInfoItem(eventId, getLangCode())
        }
    }

    private suspend fun getSpeakFromDB(eventId: Int): SeminarSpeaker {
        return withContext(Dispatchers.IO) {
            seminiarRepository.getSeminarSpeakerItem(eventId, getLangCode())
        }
    }

}
