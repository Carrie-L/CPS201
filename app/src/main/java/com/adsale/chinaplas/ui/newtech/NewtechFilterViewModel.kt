package com.adsale.chinaplas.ui.newtech

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.ExhApplication
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.data.entity.ExhibitorFilter
import com.adsale.chinaplas.utils.LogUtil
import kotlinx.coroutines.*

class NewtechFilterViewModel(private val newtechRepository: NewtechRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    var products = MutableLiveData<MutableList<ExhibitorFilter>>()
    var applications = MutableLiveData<MutableList<ExhibitorFilter>>()
    var themes = MutableLiveData<MutableList<ExhibitorFilter>>()
    var newTechs = MutableLiveData<MutableList<ExhibitorFilter>>()

    var list = MutableLiveData<MutableList<ExhApplication>>()

    var appFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var productFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var themeFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var newFilters = MutableLiveData<MutableList<ExhibitorFilter>>()

    var allFilters = mutableListOf<ExhibitorFilter>()

    val tempApplicationList: MutableList<ExhApplication> = mutableListOf()
    var entity: ExhApplication = ExhApplication()

    init {
        LogUtil.i("NewtechFilterViewModel init~ (#^.^#)")
        appFilters.value = mutableListOf()
        themeFilters.value = mutableListOf()
        newFilters.value = mutableListOf()
        allFilters = mutableListOf()
    }

    /*  新技术产品 - 筛选 */
    private var newtechType = ""

    fun setNewtechType(type: String) {
        newtechType = type
    }


    /*  ※※※※※※※※※※※  Filter list 内页列表   ※※※※※※※※※※※※※※※ */

    fun getNewtechList(type: String) {
        uiScope.launch {
            list.value = getNewtechApplicationsFromDB(type)
        }
    }

    private suspend fun getNewtechApplicationsFromDB(type: String): MutableList<ExhApplication> {
        tempApplicationList.clear()
        return withContext(Dispatchers.IO) {
            val subList = newtechRepository.getFilterSubList(type)
            for (newtech in subList) {
                entity =
                    ExhApplication(newtech.CategoryId,
                        newtech.SubNameEn,
                        newtech.SubNameTc,
                        newtech.SubNameSc,
                        null,
                        null)
                tempApplicationList.add(entity)
            }
            tempApplicationList
        }
    }

    fun onFilter() {

    }


    fun onClear() {

    }


}