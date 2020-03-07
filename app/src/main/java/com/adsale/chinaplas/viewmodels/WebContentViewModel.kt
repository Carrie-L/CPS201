package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Carrie on 2019/12/27.
 */
class WebContentViewModel : ViewModel() {
    var htmlPath = MutableLiveData<String>()

    init {

//        htmlPath.value = getWebContentHtmlPath("%s")

//        val path = "WebContent/%s/${getHtmName()}"
//        val sdPath = "${rootDir}$path"
//        val assetPath = "file:///android_asset/$path"
//
//        if (File(sdPath).exists()) {
//            htmlPath.value = "file://$sdPath"
//            LogUtil.i("webContent open sd html")
//        } else {
//            htmlPath.value = assetPath
//            LogUtil.i("webContent open asset html")
//        }
    }


}