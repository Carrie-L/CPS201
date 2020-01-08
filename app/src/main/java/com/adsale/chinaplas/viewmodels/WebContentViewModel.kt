package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.getHtmName
import java.io.File

/**
 * Created by Carrie on 2019/12/27.
 */
class WebContentViewModel : ViewModel() {
    var htmlPath = MutableLiveData<String>()

    init {

        val sdPath = "file://${rootDir}WebContent/%s/${getHtmName()}"
        val assetPath = "file:///android_asset/WebContent/%s/${getHtmName()}"

        if (File(sdPath).exists()) {
            htmlPath.value = sdPath
        } else {
            htmlPath.value = assetPath
        }
    }


}