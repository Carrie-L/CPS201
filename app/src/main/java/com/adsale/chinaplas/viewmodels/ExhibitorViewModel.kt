package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.APP
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.utils.LogUtil
import kotlinx.coroutines.*

/**
 * Created by Carrie on 2020/1/3.
 */
class ExhibitorViewModel(val app: Application, private val exhibitorRepository: ExhibitorRepository) :
    AndroidViewModel(app) {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

//    private var _exhibitorSZCaches:MutableList<Exhibitor> = m
//    private var _exhibitorHallCaches = MutableList<Exhibitor>()

    private var _exhibitors = MutableLiveData<MutableList<Exhibitor>>()
    val exhibitors: LiveData<MutableList<Exhibitor>>
        get() = _exhibitors

    private var isSortBySZ = true   // true: sz, false: hall

    init {
        uiScope.launch {
            _exhibitors.value = getAllExhibitors()
            LogUtil.i("_exhibitors: ${_exhibitors.value?.size}, ${exhibitors.value.toString()}")
        }
    }

    private suspend fun getAllExhibitors(): MutableList<Exhibitor> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getAllExhibitorList()
        }
    }

    fun onStar(entity: Exhibitor, pos: Int) {
        entity.IsFavourite = if (entity.IsFavourite == 0) 1 else 0
        LogUtil.i("onStar: IsFavourite=${entity.IsFavourite}, pos=${pos}, name = ${entity.getCompanyName()}")
        entity.isStared.set(!entity.isStared.get())
        _exhibitors.value!![pos] = entity
        uiScope.launch {
            withContext(Dispatchers.IO) {
                exhibitorRepository.updateExhibitorItem(entity)
            }
        }
    }

    /**
     * 按拼音排序
     */
    fun onSortAZ() {
        if(!isSortBySZ){
//            _exhibitors
        }
    }

    /**
     * 按展馆排序
     */
    fun onSortHall() {



    }

}