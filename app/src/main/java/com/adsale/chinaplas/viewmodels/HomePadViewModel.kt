package com.adsale.chinaplas.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.utils.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class HomePadViewModel() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Pad Fragment"
    }
    val text: LiveData<String> = _text

    companion object {
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 5000L   // viewpager 5s 自动滚动到下一张
    }

    private var _rollNext = MutableLiveData<Boolean>()
    val rollNext: LiveData<Boolean> = _rollNext
    private lateinit var timer: CountDownTimer

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<ByteArray>()

    // The external immutable LiveData for the response String
    val response: LiveData<ByteArray>
        get() = _response

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    init {
        //        downloadTxt("MainBannerInfo.txt")
        LogUtil.i(">>> home view model init")
        //        parseMainBannerTxt("$fileAbsPath/MainBannerInfo.txt")
        addCountDownTime()
    }

    fun downloadTxt(fileName: String) {
        coroutineScope.launch {
            val getDeferred = CpsApi.retrofitService.downTxtAsync(fileName)
            try {
                // Await the completion of our Retrofit request
                val responseBody = getDeferred.await()
                _response.value = responseBody.bytes()
                responseBody.close()
                LogUtil.i("downloadTxt::Success:${_response.value}")
            } catch (e: Exception) {
                LogUtil.e("downloadTxt::Failure: ${e.message}")
            }
        }
    }






    fun getMainIcons() {
        coroutineScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred = CpsApi.retrofitService.getMainIcons()
            try {
                // Await the completion of our Retrofit request
                val responseBody = getDeferred.await()
                //                _response.value = responseBody.bytes()
                val str = responseBody.string()
                val endMill1 = System.currentTimeMillis()
                LogUtil.i("spend Time :${endMill1 - startMill}")
                val endMill2 = System.currentTimeMillis()
                LogUtil.i("getMainIcons::Success")
                responseBody.close()
                val endMill = System.currentTimeMillis()
                LogUtil.i("spend Time :${endMill2 - startMill}")
            } catch (e: Exception) {
                LogUtil.e("getMainIcons::Failure: ${e.message} ms")
            }
        }
    }

    /**
     * @param absPath .../data/data/files/MainBannerInfo.txt
     */
    //    private fun parseMainBannerTxt(absPath: String) {
    //        val startMills = System.currentTimeMillis()
    //        val type = Types.newParameterizedType(List::class.java, MainBanner::class.java)
    //        val jsonAdapter: JsonAdapter<List<MainBanner>> = moshi.adapter(type)
    //        if (File(absPath).exists()) {
    //            _topBanners.value = jsonAdapter.fromJson(readFile(absPath))
    //        } else {
    //            _topBanners.value = jsonAdapter.fromJson(readAssetFile("files/MainBannerInfo.txt"))
    //        }
    //        val endMills = System.currentTimeMillis()
    //        LogUtil.i("parseMainBannerTxt spend time ${endMills - startMills} ms")
    //    }

    //    private suspend fun parseMainBannerTxt(absPath: String): List<MainBanner> {
    //        return withContext(Dispatchers.IO) {
    //            val type = Types.newParameterizedType(List::class.java, MainBanner::class.java)
    //            val jsonAdapter: JsonAdapter<List<MainBanner>> = moshi.adapter(type)
    //            val list: List<MainBanner>
    //            if (File(absPath).exists()) {
    //                list = jsonAdapter.fromJson(readFile(absPath))!!
    //            } else {
    //                list = jsonAdapter.fromJson(readAssetFile("files/MainBannerInfo.txt"))!!
    //            }
    //            list
    //        }
    //    }

    /**
     * 倒计时
     */
    private fun addCountDownTime() {
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {   //  millisUntilFinished / ONE_SECOND = 4,3,2,1,0
                //                LogUtil.i("**** onTick: ${millisUntilFinished / ONE_SECOND}")
                _rollNext.value = false
            }

            override fun onFinish() {
                _rollNext.value = true
                addCountDownTime()  // 无限轮播
            }
        }
        timer.start()
    }

    fun stopTimer() {
        _rollNext.value = false
        timer.cancel()
    }

    fun startTimer() {
        if (_rollNext.value == false) {
            LogUtil.i("* startTimer *")
            timer.start()
            _rollNext.value = true
        }
    }


}