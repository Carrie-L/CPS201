package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.SeminarInfo
import com.adsale.chinaplas.data.dao.SeminarRepository
import com.adsale.chinaplas.data.dao.SeminarSpeaker
import com.adsale.chinaplas.helper.ADHelper
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
    var tabClick = MutableLiveData(1)
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

    var amVisible = MutableLiveData(true)
    var pmVisible = MutableLiveData(true)


    init {
        LogUtil.i("SeminarViewModel init~~~")
    }

    fun onFunctionClick(index: Int) {
        if (index <= 3) {
            tabClick.value = index
            setSeminarTabIndex(index)
        } else {
            btnClick.value = index
        }
    }

    fun onAmClick() {
        setSeminarTimeIndex(getCurrentDate(), 0)
        currentIsAm.value = true
//        if (seminarAmListCaches.size == 0) {
////            getSeminarTimeList(true)
//        } else {
//            seminarList.value = seminarAmList
//        }

        val filter = getSPSeminarFilter(getCurrentDate())
        if (filter.isNotEmpty()) {
            getFilterSeminars(filter)
        } else {
            seminarList.value = seminarAmList
        }


    }

    fun onPmClick() {
        setSeminarTimeIndex(getCurrentDate(), 1)
        currentIsAm.value = false
//        if (seminarPmListCaches.size == 0) {
//            getSeminarTimeList(false)
//        } else {
//            seminarList.value = seminarPmList
//        }


        val filter = getSPSeminarFilter(getCurrentDate())
        LogUtil.i("onPmClick. getCurrentDate=${getCurrentDate()}. filter=$filter")
        if (filter.isNotEmpty()) {
            getFilterSeminars(filter)
        } else {
            seminarList.value = seminarPmList
        }


    }

    fun getSeminarList() {
        uiScope.launch {
            seminarAmListCaches.clear()
            seminarAmList = getSeminarTimeListFromDB(true) as MutableList<SeminarInfo>
            seminarAmListCaches.addAll(seminarAmList)
            amVisible.value = seminarAmListCaches.isNotEmpty()

            seminarPmListCaches.clear()
            seminarPmListCaches.addAll(getSeminarTimeListFromDB(false))
            seminarPmList.addAll(seminarPmListCaches)
            pmVisible.value = seminarPmListCaches.isNotEmpty()

            if (amVisible.value!!) {
                currentIsAm.value = true
                seminarList.value = seminarAmList
                setSeminarTimeIndex(getCurrentDate(), 0)
            } else {
                currentIsAm.value = false
                seminarList.value = seminarPmList
                setSeminarTimeIndex(getCurrentDate(), 1)
            }

            LogUtil.i("getSeminarList: date=${getCurrentDate()}, isAm=${amVisible.value}")

        }


    }


//    fun getSeminarTimeList(isAm: Boolean) {
//        uiScope.launch {
//            if (isAm) {
//                seminarAmListCaches.clear()
//                seminarAmList = getSeminarTimeListFromDB(isAm) as MutableList<SeminarInfo>
//                seminarAmListCaches.addAll(seminarAmList)
//                seminarList.value = seminarAmList
//                amVisible.value = seminarAmListCaches.isNotEmpty()
//
//                // 上午没有数据, 则默认显示下午
//                if (seminarAmListCaches.isEmpty()) {
//                    getSeminarTimeList(false)
//                    currentIsAm.value = false
//                }
//
//            } else {
//                seminarPmListCaches.clear()
//                seminarPmListCaches.addAll(getSeminarTimeListFromDB(isAm))
//                seminarPmList.addAll(seminarPmListCaches)
//                seminarList.value = seminarPmList
//                pmVisible.value = seminarPmListCaches.isNotEmpty()
//                currentIsAm.value = !pmVisible.value!!
//            }
//            LogUtil.i("seminarList=${seminarList.value?.size}")
//        }
//    }

    fun resetList() {
        currentIsAm.value = amVisible.value!!
        resetSeminarTimeIndex(if (amVisible.value!!) 0 else 1)

        if (amVisible.value!!) {
            if (seminarAmListCaches.isNotEmpty()) {
                seminarList.value = seminarAmListCaches
            } else {
//                getSeminarTimeList(true)
            }
        } else {
            if (seminarPmListCaches.isNotEmpty()) {
                seminarList.value = seminarPmListCaches
            } else {
//                getSeminarTimeList(false)
            }
        }


    }

    private suspend fun getSeminarTimeListFromDB(isAm: Boolean): List<SeminarInfo> {
        LogUtil.i("getSeminarTimeListFromDB: getCurrentDate=${getCurrentDate()}")
        return withContext(Dispatchers.IO) {
            val list = seminiarRepository.getSeminarTimeList(
                getLangCode(), getCurrentDate(), isAm)
            setAdList(list)
        }
    }

    fun getFilterSeminars(filter: String) {
        uiScope.launch {
            if (currentIsAm.value!!) {
                seminarAmList = getFilterSeminarTimeListFromDB(filter) as MutableList<SeminarInfo>
                seminarList.value = seminarAmList
            } else {
                seminarPmList = getFilterSeminarTimeListFromDB(filter) as MutableList<SeminarInfo>
                seminarList.value = seminarPmList
            }
            LogUtil.i("getFilterSeminars:  seminarList=${seminarList.value?.size}")
        }
    }

    private suspend fun getFilterSeminarTimeListFromDB(filter: String): List<SeminarInfo> {
        LogUtil.i("getFilterSeminarTimeListFromDB: getCurrentDate=${getCurrentDate()}")
        return withContext(Dispatchers.IO) {
            val list = seminiarRepository.getFilterSeminars(getLangCode(),
                getCurrentDate(), currentIsAm.value!!, filter)
            setAdList(list)
        }
    }

    private fun setAdList(list: MutableList<SeminarInfo>): List<SeminarInfo> {
        val adHelper = ADHelper.getInstance()
        val d8List = adHelper.d8List()
        for (d8 in d8List) {
            for ((i, entity) in list.withIndex()) {
                if (entity.CompanyID == d8.companyID && (d8.isAm == 1) == (entity.Time!! < "12:00") && (entity.Date!!.contains(
                        d8.date))) {
                    entity.isAder = true
                    entity.logoImage = d8.image_logo
                    list.removeAt(i)
                    list[0] = entity
                    break
                }
            }
        }
        return list
    }

    fun getCurrentDate(): String {
        LogUtil.i("getCurrentDate() currentDateSelect=${currentDateSelect.value}")
        return currentDateSelect.value!!.let {
            when (it) {
                2 -> "%$DATE_2%"
                3 -> "%$DATE_3%"
                1 -> "%$DATE_1%"
                else -> "all"
            }
        }
    }

    /*技术交流会详情页*/
    var infoEntity = MutableLiveData<SeminarInfo>()
    var speakerEntity = MutableLiveData<SeminarSpeaker>()

    fun getDetailInfo(eventId: Int) {
        uiScope.launch {
            speakerEntity.value = getSpeakFromDB(eventId)
            infoEntity.value = getInfoFromDB(eventId)
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
