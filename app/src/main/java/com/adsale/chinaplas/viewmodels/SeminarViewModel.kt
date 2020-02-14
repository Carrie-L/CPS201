package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Carrie on 2020/2/14.
 */
class SeminarViewModel : ViewModel() {
    /**
     * @am 1
     * @pm 2
     * @筛选 3
     * @重置 5
     */
    var btnClick = MutableLiveData(0)


    fun onFunctionClick(index: Int) {
        btnClick.value = index
    }

}
