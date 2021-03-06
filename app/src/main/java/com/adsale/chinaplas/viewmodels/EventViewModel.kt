package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adsale.chinaplas.data.dao.ConcurrentEvent
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.utils.getSPEventFilter
import com.adsale.chinaplas.utils.setEventTabIndex
import com.baidu.speech.utils.LogUtil
import kotlinx.coroutines.*

/**
 * Created by Carrie on 2020/2/13.
 */
class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job
    var tabClickIndex = MutableLiveData(1)  // 左边1，右边2
    var barClick = MutableLiveData(0)
    var events = MutableLiveData<List<ConcurrentEvent>>()

    init {
        viewModelScope.launch {

        }
    }

    fun getOverallEvents() {
        uiScope.launch {
            events.value = getOverallEventsFromDB()
            LogUtil.i("getOverallEvents=${events.value?.size},,, ${events.value?.toString()}")
        }
    }

    fun getDateEvents(date: String) {
        uiScope.launch {
            events.value = getDateEventsFromDB(date)
            LogUtil.i("getDateEvents=${events.value?.size},,, ${events.value?.toString()}")
        }
    }

    private suspend fun getOverallEventsFromDB(): List<ConcurrentEvent> {
        return withContext(Dispatchers.IO) {
            val filter = getSPEventFilter()
            LogUtil.i("getOverallEventsFromDB: filter=$filter")
            if (filter.isEmpty()) {
                eventRepository.getOverallList()
            } else {
                eventRepository.getFilterOverallList(filter)
            }
        }
    }

    private suspend fun getDateEventsFromDB(date: String): List<ConcurrentEvent> {
        return withContext(Dispatchers.IO) {
            val filter = getSPEventFilter()
            if (filter.isEmpty()) {
                eventRepository.getDateEventList(date)
            } else {
                eventRepository.getFilterDateEventList(filter, date)
            }
        }
    }

    fun onClick(index: Int) {
        setEventTabIndex(index)
        barClick.value = index
    }

    /**
     * 大标签
     */
    fun onTabClick(index: Int) {
        tabClickIndex.value = index
    }


}