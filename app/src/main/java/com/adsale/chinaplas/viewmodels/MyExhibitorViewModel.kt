package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getMemberId
import com.adsale.chinaplas.utils.getVisitorId
import okhttp3.FormBody

class MyExhibitorViewModel(private val exhibitorRepository: ExhibitorRepository) : ViewModel() {

    var exhibitors: LiveData<List<Exhibitor>> = exhibitorRepository.getMyExhibitorList()
    var nodataVisible = Transformations.map(exhibitors) {
        it.isEmpty()
    }

    init {


    }

    /**
     * 登录后才能进行同步
     */
    fun sync(){

    }

}
