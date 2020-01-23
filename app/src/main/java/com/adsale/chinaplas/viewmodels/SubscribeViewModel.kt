package com.adsale.chinaplas.viewmodels

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.RequestBody
import java.lang.Exception

class SubscribeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is share Fragment"
    }
    val text: LiveData<String> = _text

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    var company = MutableLiveData<String>("")
    var name = MutableLiveData<String>("")
    var email = MutableLiveData<String>("")

    var checkStatus = MutableLiveData(-1)

    var result = MutableLiveData(-1)  // 0 失敗， 1 成功

    fun onSubmit() {
        if (checkEmpty()) {
            return
        }
        checkStatus.value = 98  // 進度條
        uiScope.launch {
            checkStatus.value = if (subscribe()) 100 else 99   // 訂閱結果
            i("subscribe= ${result.value}")
        }
    }

    private suspend fun subscribe(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = CpsApi.retrofitService.subcribeSubmit(getLangStr(), requestBody()).await()
                val res: String = response.string()
                i("res=$res")
                res.isNotEmpty() && res.contains("callSuccess()")
            } catch (e:Exception) {
                e.printStackTrace()
                e.message?.let { i(it) }
                false
            }
        }
    }

    private fun requestBody(): RequestBody {
        return FormBody.Builder()
            .add("CompName", company.value!!)
            .add("EName", name.value!!)
            .add("Email", email.value!!)
            .add("rad", getLangCode())
            .build()
    }

    fun onReset() {
       reset()
    }

    fun reset(){
        name.value = ""
        company.value = ""
        email.value = ""
    }

    fun onPrivacy() {
        checkStatus.value = 101
    }

    private fun checkEmpty(): Boolean {
        if (TextUtils.isEmpty(company.value)) {
            checkStatus.value = COMPANY_EMPTY
            return true
        }
        if (TextUtils.isEmpty(name.value)) {
            checkStatus.value = NAME_EMPTY
            return true
        }
        if (!checkYou(name.value!!)) {
            checkStatus.value = NAME_INVALID
            return true
        }
        if (TextUtils.isEmpty(email.value)) {
            checkStatus.value = EMAIL_EMPTY
            return true
        }
        if (!checkEmail(email.value!!)) {
            checkStatus.value = EMAIL_INVALID
            return true
        }
        return false
    }


}