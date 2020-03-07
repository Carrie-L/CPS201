package com.adsale.chinaplas.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*

/**
 * Created by Carrie on 2020/3/6.
 */
class GlobalViewModel(private val htmlTextRepository: HtmlTextRepository,
                      private val exhibitorDao: ExhibitorDao,
                      private val newtechDao: NewtechDao,
                      private val seminarDao: SeminarDao
) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    var resultList = MutableLiveData<List<HtmlText>>()

    var tempHtmls: MutableList<HtmlText> = mutableListOf()
    var tempExhibitors: MutableList<HtmlText> = mutableListOf()
    var tempNewtechs: MutableList<HtmlText> = mutableListOf()
    var tempSeminars: MutableList<HtmlText> = mutableListOf()


    //    var tagVisible = Transformations.map(resultList) {
//        if (it.isEmpty()) View.VISIBLE else View.GONE
//    }
//    var resultVisible = Transformations.map(resultList) {
//        if (it.isEmpty()) View.GONE else View.VISIBLE
//    }
//    var nodataVisible = Transformations.map(resultList) {
//        if (it.isEmpty() && isSearching.value==true) View.VISIBLE else View.GONE
//    }

    var isSearching = MutableLiveData(false)
    var clearVisible = Transformations.map(isSearching) {
        if (isSearching.value == true) View.VISIBLE else View.GONE
    }
    var searchVisible = Transformations.map(isSearching) {
        if (isSearching.value == true) View.GONE else View.VISIBLE
    }


    init {
        insertToDB()
    }

    private fun insertToDB() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                htmlTextRepository.insertItemHtmlText(parseJson(HtmlText::class.java,
                    readAssetFile("text/DXI.txt")) as HtmlText)
                htmlTextRepository.insertItemHtmlText(parseJson(HtmlText::class.java,
                    readAssetFile("text/Opening.txt")) as HtmlText)
                htmlTextRepository.insertItemHtmlText(parseJson(HtmlText::class.java,
                    readAssetFile("text/Industry4.txt")) as HtmlText)
                htmlTextRepository.insertItemHtmlText(parseJson(HtmlText::class.java,
                    readAssetFile("text/Medical.txt")) as HtmlText)
                htmlTextRepository.insertItemHtmlText(parseJson(HtmlText::class.java,
                    readAssetFile("text/TechTalk.txt")) as HtmlText)
                htmlTextRepository.insertItemHtmlText(parseJson(HtmlText::class.java,
                    readAssetFile("text/Scope.txt")) as HtmlText)
            }
        }
    }

    fun searchHtmlText(keyword: String) {
        uiScope.launch {
            tempHtmls.clear()
            tempExhibitors.clear()
            tempNewtechs.clear()
            tempSeminars.clear()

            tempHtmls = searchFromDB(keyword) as MutableList<HtmlText>
            searchFromExhibitorDB(keyword)
            searchFromNewtechDB(keyword)
            searchFromSeminarDB(keyword)

            tempHtmls.addAll(tempExhibitors)
            tempHtmls.addAll(tempNewtechs)
            tempHtmls.addAll(tempSeminars)

            resultList.value = tempHtmls

        }
    }

    private suspend fun searchFromDB(keyword: String): List<HtmlText> {
        return withContext(Dispatchers.IO) {
            htmlTextRepository.searchHtmlText(keyword)
        }
    }

    private suspend fun searchFromExhibitorDB(keyword: String) {
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val exhibitors = when (getCurrLanguage()) {
                LANG_SC -> exhibitorDao.querySearchSC(keyword)
                LANG_EN -> exhibitorDao.querySearchEN(keyword)
                else -> exhibitorDao.querySearchTC(keyword)
            }
            LogUtil.i("exhibitors = ${exhibitors.size}")
            var htmlTextEntity: HtmlText
            for (entity in exhibitors) {
                htmlTextEntity =
                    HtmlText(entity.CompanyID, SEARCH_GROUP_EXHIBITOR, entity.CompanyNameCN!!, entity.CompanyNameTW!!,
                        entity.CompanyNameEN!!, getExhibitorTitle(), entity.BoothNo!!)
                tempExhibitors.add(htmlTextEntity)
            }
            val endTime = System.currentTimeMillis()
            LogUtil.i("searchFromExhibitorDB spend ${endTime - startTime} ms")
        }
    }

    private suspend fun searchFromNewtechDB(keyword: String) {
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val newtechs = newtechDao.searchNewtech(keyword)
            LogUtil.i("newtechs = ${newtechs.size}")
            var htmlTextEntity: HtmlText
            for (entity in newtechs) {
                htmlTextEntity =
                    HtmlText(entity.RID, SEARCH_GROUP_NEW_TECH, entity.Product_Name_SC!!, entity.Product_Name_TC!!,
                        entity.Product_Name_EN!!, getNewtechTitle(), entity.getCompanyName())
                tempNewtechs.add(htmlTextEntity)
            }
            val endTime = System.currentTimeMillis()
            LogUtil.i("searchFromNewtechDB spend ${endTime - startTime} ms")
        }
    }

    private suspend fun searchFromSeminarDB(keyword: String) {
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val seminars = seminarDao.searchSeminar(keyword)
            LogUtil.i("seminars = ${seminars.size}")
            var htmlTextEntity: HtmlText
            for (entity in seminars) {
                htmlTextEntity =
                    HtmlText(entity.EventID.toString(), SEARCH_GROUP_SEMINAR, entity.Topic!!, entity.Topic!!,
                        entity.Topic!!, getSeminarTitle(), entity.PresentCompany!!)
                tempSeminars.add(htmlTextEntity)
            }
            val endTime = System.currentTimeMillis()
            LogUtil.i("searchFromSeminarDB spend ${endTime - startTime} ms")
        }
    }


}