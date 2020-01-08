package com.adsale.chinaplas.viewmodels

/**
 * Created by Carrie on 2019/11/16.
 */
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.adapters.OnItemClickListener

import com.adsale.chinaplas.data.dao.RegOptionData
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*
import java.lang.StringBuilder

/**
 * Created by Carrie on 2019/11/4.
 *
 */
class RegPickViewModel(val regRepo: RegisterRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    // 需要返回： 公司业务的各种code, text, other.
    /**
     * 公司业务：
     * code, name
     * otherCode, otherName, otherInputText
     *
     * 国家地区：
     * countryCode,provinceCode,cityCode,telCountryCode, country,province,city
     */

    /*  对外暴露的数据   */


    /* 接收的数据  code */
    var dialogDismiss = MutableLiveData<Boolean>(false)

    // -- 公司业务 --
    var services = MutableLiveData<MutableList<RegOptionData>>()
    var serviceLabels = MutableLiveData<MutableList<RegOptionData>>()
    private var serviceLabelList: MutableList<RegOptionData> = mutableListOf()
    // 显示在Fragment界面的文字
    var serviceText = Transformations.map(serviceLabels) {
        it.toString().replace("[", "").replace("]", "")
    }
    var serviceAllText = Transformations.map(serviceLabels) { labels ->
        val sb = StringBuilder()
        for (item in labels) {
            if (sb.isNotEmpty()) {
                sb.append(", ")
            }
            sb.append(item.getName())
//            sb.append(ser)
//           when(item.DetailCode){
//               "2008" -> sb.append(car)
//           }
        }

    }

    var isProductSelectTitleVisible = Transformations.map(serviceLabels) {
        if (it.isEmpty()) View.VISIBLE else View.GONE
    }
    var otherService = MutableLiveData<KV>()

    init {
        LogUtil.i("init RegPickViewModel~~ currLang=${getCurrLanguage()}")
        serviceLabels.value = mutableListOf()
        getProducts()
    }

    /*  --------   公司业务    -----  */

    private fun getProducts() {
        uiScope.launch {
            services.value = getProductsFromDB()
            LogUtil.i("getProducts=${services.value?.size},${services.value.toString()}")
            processSpProductData()
        }
    }

    /**
     * 取出sp中保存的productLabels数据，更改products和productLabels的isChecked值
     */
    private fun processSpProductData() {
        val spLabels = getRegServiceCode().split(",")
        var i = 0
        serviceLabelList.clear()
        services.value?.let { list ->
            for (item in list) {
                for (j in spLabels.indices) {
                    if (item.DetailCode == spLabels[j]) {
                        item.isChecked.set(true)
                        serviceLabelList.add(item)
                        serviceLabels.value = serviceLabelList
                        if (item.isOther()) {
                            otherService.value = KV(item, true)
                        }
                        break
                    }
                    i++
                }
            }
        }
    }

    private suspend fun getProductsFromDB(): MutableList<RegOptionData> {
        return withContext(Dispatchers.IO) {
            regRepo.getProducts()
        }
    }

    val onProductListItemClickListener = OnItemClickListener { item, _ ->
        item as RegOptionData
        if (!item.isProductParent()) {
            if (!item.isChecked.get()!!) {
                item.isChecked.set(true)
                serviceLabelList.add(item)
                serviceLabels.value = serviceLabelList
                if (item.isOther()) {
                    otherService.value = KV(item, true)
                }
            } else {
                item.isChecked.set(false)
                for (entity in serviceLabelList) {
                    if (entity.objectId == item.objectId) {
                        serviceLabelList.remove(entity)
                        serviceLabels.value = serviceLabelList
                        if (item.isOther()) {
                            otherService.value = KV(item, false)
                        }
                        break
                    }
                }
            }
        }
        LogUtil.i("productLabels.size= ${serviceLabels.value?.size}")
    }

    val labelItemClickListener = OnItemClickListener { item, _ ->
        item as RegOptionData
        val size = services.value!!.size
        var entity: RegOptionData
        for (i in 0 until size) {
            entity = services.value!![i]
            if (entity.objectId == item.objectId) {
                entity.isChecked.set(false)
                services.value!![i] = entity
                serviceLabelList.remove(item)
                serviceLabels.value = serviceLabelList
                if (item.isOther()) {
                    //                    otherService.value = KV(item.getName(), false)
                    otherService.value = KV(item, false)
                }
                break
            }
        }
    }

    fun onDialogConfirm() {
        dialogDismiss.value = true
    }


    private suspend fun getProductUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            val updatedAt = regRepo.getRegOptionLastUpdateTime()
            LogUtil.i("product updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) {
                timeAddOneSecond(updatedAt)
            } else {
                "2019-01-01 00:00:00"
            }
        }
    }

    private fun getProductsFromBmob(updatedAt: String) {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred = CpsApi.retrofitService.getProductsAsync(
                "{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
                500
            )
            try {
                val responseBody = getDeferred.await()
                var content = responseBody.string().replace("{\"results\":", "")
                content = content.substring(0, content.length - 1)

                LogUtil.i("content=$content")

                val list: List<RegOptionData>? = parseListJson(RegOptionData::class.java, content)
                withContext(Dispatchers.IO) {
                    list?.let {
                        LogUtil.i("it=$it")
                        regRepo.insertRegOptionDataAll(it)
                    }
                }
                LogUtil.i("getProductsFromBmob: ${list?.size}")
                val endMill = System.currentTimeMillis()
                LogUtil.i("getProductsFromBmob spend Time :${endMill - startMill} ms")
            } catch (e: Exception) {
                LogUtil.e("getProductsFromBmob::Failure: ${e.message} ")
            }

            getProducts()

        }
    }

    /**
     * 下载所有bmob RegOptionData
     */
    private fun downloadAllRegOptions() {
        uiScope.launch {
            //            clearRegOptionData()
            val time = getProductUpdateTime()
            LogUtil.i("time =$time")
            getProductsFromBmob(time)   //
        }
    }

    /**
     * 下载所有bmob country数据
     */
    private fun downloadAllCountries() {
        uiScope.launch {
            val time = getCountryUpdateTime()
            LogUtil.i("time =$time")

            for (i in 0 until 3500 step 500) {
                LogUtil.i("开始下载：i=$i")
                getCountriesFromBmob(i, time)
            }

        }

    }

    private suspend fun getCountryUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            var updatedAt = regRepo.getCountryLastUpdateTime()
            LogUtil.i("updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) {
                timeAddOneSecond(updatedAt)
            } else {
                "2019-01-01 00:00:00"
            }
        }
    }

    /**
     * 分页查询
     * skip limit的意思是：
     * 例如，skip=200,limit=500
     * 跳过前200行，获取第201 - （200+500）行的数据
     */
    fun getCountriesFromBmob(skip: Int, updatedAt: String) {
//        uiScope.launch {
//            val startMill = System.currentTimeMillis()
//            val getDeferred = CpsApi.retrofitService.getCountry(
//                "{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
//                500,
//                skip
//            )
//            try {
//                val responseBody = getDeferred.await()
//                var content = responseBody.string().replace("{\"results\":", "")
//                content = content.substring(0, content.length - 1)
//                val countries: List<CountryJson>? = parseListJson(CountryJson::class.java, content)
//                withContext(Dispatchers.IO) {
//                    countries?.let {
//                        regRepo.insertCountryAll(it)
//                    }
//                }
//                LogUtil.i("getCountriesFromBmob: ${countries?.size}")
//
//                val endMill = System.currentTimeMillis()
//                LogUtil.i("getCountriesFromBmob spend Time :${endMill - startMill} ms")
//            } catch (e: Exception) {
//                LogUtil.e("getCountriesFromBmob::Failure: ${e.message} ")
//            }
    }
//    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()

    }


}