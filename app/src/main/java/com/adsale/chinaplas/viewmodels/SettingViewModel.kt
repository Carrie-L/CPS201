package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.utils.LogUtil

class SettingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tools Fragment"
    }
    val text: LiveData<String> = _text

    var langClick = MutableLiveData(false)


    init {

    }


    fun onLangClick() {
        langClick.value = true
        LogUtil.i("onLangClick=${langClick.value}")
    }

    fun onShare() {

    }

    fun onLinkWebsite() {

    }

    fun onResetAll() {

    }

    fun onPrivacy() {

    }

    fun onUseItems() {

    }


}