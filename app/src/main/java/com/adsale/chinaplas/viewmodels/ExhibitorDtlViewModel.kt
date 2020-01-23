package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getCurrentTime
import kotlinx.coroutines.*

/**
 * Created by Carrie on 2020/1/9.
 */
class ExhibitorDtlViewModel(private val exhibitorRepository: ExhibitorRepository, private val companyID: String) :
    ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

    var company = MutableLiveData<Exhibitor>()

    private var _isAD = MutableLiveData<Boolean>(false)
    val isAD: MutableLiveData<Boolean>
        get() = _isAD

    var tabIndex = MutableLiveData(0)

    var applications = MutableLiveData<List<ExhApplication>>()
    var industries = MutableLiveData<List<ExhIndustry>>()
    private var _itemNavigation = MutableLiveData<KV>()
    val itemNavigation: MutableLiveData<KV>
        get() = _itemNavigation

    var aboutVisible = MutableLiveData(false)
    var descVisible = MutableLiveData(false)
    var industryVisible = MutableLiveData(false)
    var appVisible = MutableLiveData(false)

    var getDataFinish = MutableLiveData(false)

    var starExhibitor = Transformations.map(company) { entity ->
        entity.IsFavourite == 1
    }

    var hasStarUpdate = MutableLiveData<Boolean>(false)

    init {
        uiScope.launch {
            LogUtil.i("--------Start DB--------")
            company.value = getCompanyInfoFromDB()
            LogUtil.i("--------company DB--------${company.toString()}")
            applications.value = getApplicationsFromDB()
            LogUtil.i("--------applications DB--------${applications.value?.size}")
            industries.value = getIndustriesFromDB()
            LogUtil.i("--------industries DB--------${industries.value?.size}")
            LogUtil.i("--------Finish DB--------")
            getDataFinish.value = true
        }
    }

    fun insertToHistory() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val historyEntity = ExhibitorHistory(
                    null,
                    company.value!!.CompanyID,
                    company.value!!.CompanyNameEN,
                    company.value!!.CompanyNameTW,
                    company.value!!.CompanyNameCN,
                    company.value!!.BoothNo,
                    getCurrentTime(),
                    1
                )
                exhibitorRepository.insertToHistory(historyEntity)
            }
        }
    }

    private suspend fun getCompanyInfoFromDB(): Exhibitor {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getCompanyItem(companyID)
        }
    }

    private suspend fun getIndustriesFromDB(): List<ExhIndustry> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getIndustries(companyID)
        }
    }

    private suspend fun getApplicationsFromDB(): List<ExhApplication> {
        return withContext(Dispatchers.IO) {
            exhibitorRepository.getApplications(companyID)
        }
    }

    fun setItemNavigationValue(kv: KV) {
        _itemNavigation.value = kv
    }

    fun finishItemNavigation() {
        _itemNavigation.value = null
    }

    fun addMyExhibitor() {
        val entity = company.value
        entity!!.IsFavourite = if (entity.IsFavourite == 1) 0 else 1
        company.value = entity
        hasStarUpdate.value = true
        updateExhibitorItem()
    }

    private fun updateExhibitorItem() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                exhibitorRepository.updateExhibitorItem(company.value!!)
            }
        }
    }

    fun onTabClick(index: Int) {
        tabIndex.value = index
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }


}