package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.ExhibitorDao
import com.adsale.chinaplas.data.dao.ExhibitorHistory
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.utils.getTodayDate
import com.adsale.chinaplas.utils.getYesterdayDate
import kotlinx.coroutines.*

/**
 * Created by Carrie on 2020/1/21.
 */
class ExhibitorHistoryViewModel(private val exhibitorDao: ExhibitorDao) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    var list = MutableLiveData<MutableList<ExhibitorHistory>>()

    private var todayList = mutableListOf<ExhibitorHistory>()
    private var yesterdaylist = listOf<ExhibitorHistory>()
    private var pastList = listOf<ExhibitorHistory>()
//    var yesterdaylist = List<ExhibitorHistory>()
//    var pastList = List<ExhibitorHistory>()

    init {

    }

    fun getList() {
        uiScope.launch {
            //            list.value = getAllHistoryFromDB()
            i("get list")
            val list1 = getAllHistoryFromDB()
            i("list1=${list1.size},,,${list1.toString()}")
            todayList = getTodayFromDB()
            i("todayList=${todayList.size}")
            yesterdaylist = getYesterdayFromDB()
            i("yesterdaylist=${yesterdaylist.size}")
            pastList = getPastFromDB()
            i("pastList=${pastList.size}")

//            for(entity in todayList){
//                entity.isTypeLabel.set(1)
//                todayList[]
//            }



            todayList.addAll(yesterdaylist)
            todayList.addAll(pastList)
            list.value = todayList
        }
    }

    private suspend fun getAllHistoryFromDB(): List<ExhibitorHistory> {
        return withContext(Dispatchers.IO) {
            exhibitorDao.getAllHistories()
        }
    }

    private suspend fun getTodayFromDB(): MutableList<ExhibitorHistory> {
        return withContext(Dispatchers.IO) {
            i("getTodayDate=${getTodayDate()}")
            exhibitorDao.getRecentHistories("%${getTodayDate()}%")
        }
    }

    private suspend fun getYesterdayFromDB(): List<ExhibitorHistory> {
        return withContext(Dispatchers.IO) {
            i("getYesterdayDate=${getYesterdayDate()}")
            exhibitorDao.getRecentHistories("%${getYesterdayDate()}%")
        }
    }

    private suspend fun getPastFromDB(): List<ExhibitorHistory> {
        return withContext(Dispatchers.IO) {
            exhibitorDao.getPastHistories(getYesterdayDate())
        }
    }

}