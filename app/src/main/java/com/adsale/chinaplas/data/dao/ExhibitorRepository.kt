package com.adsale.chinaplas.data.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.Insert
import androidx.sqlite.db.SimpleSQLiteQuery
import com.adsale.chinaplas.data.entity.ExhibitorFilter
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import java.util.*

/**
 * Created by Carrie on 2020/1/3.
 */
class ExhibitorRepository private constructor(
    private val exhibitorDao: ExhibitorDao,
    private val industryDao: IndustryDao,
    private val applicationDao: ApplicationDao
) {

    fun insertAll(list: List<Exhibitor>) = exhibitorDao.insertExhibitorAll(list)

    fun updateExhibitorItem(entity: Exhibitor) = exhibitorDao.updateExhibitorItem(entity)

//    fun updateExhibitorItem(entity: MutableLiveData<Exhibitor>) = exhibitorDao.updateExhibitorItemLive(entity)

    fun getAllExhibitorList() = when (getCurrLanguage()) {
        LANG_SC -> exhibitorDao.getAllExhibitorsSC()
        LANG_EN -> exhibitorDao.getAllExhibitorsEN()
        else -> exhibitorDao.getAllExhibitorsTC()
    }

    fun getMyExhibitorList() = when (getCurrLanguage()) {
        LANG_SC -> exhibitorDao.getMyExhibitorsSC()
        LANG_EN -> exhibitorDao.getMyExhibitorsEN()
        else -> exhibitorDao.getMyExhibitorsTC()
    }


//    fun queryExhibitors(text: String) = when (getCurrLanguage()) {
//        LANG_SC -> exhibitorDao.querySearchSC(text)
//        LANG_EN -> exhibitorDao.querySearchEN(text)
//        else -> exhibitorDao.querySearchTC(text)
//    }

    fun filterExhibitors(filterSql: String) = run {
        val query = SimpleSQLiteQuery(filterSql)
        exhibitorDao.filterExhibitors(query)
    }


    fun getAllExhibitors() = exhibitorDao.getAllExhibitors()

    fun getCompanyItem(companyID: String) = exhibitorDao.getCompanyItem(companyID)

    fun getApplicationInExhibitors(id: String) = when (getCurrLanguage()) {
        LANG_SC -> exhibitorDao.getApplicationInExhibitorsSC(id)
        LANG_EN -> exhibitorDao.getApplicationInExhibitorsEN(id)
        else -> exhibitorDao.getApplicationInExhibitorsTC(id)
    }

    fun getIndustryInExhibitors(id: String) = when (getCurrLanguage()) {
        LANG_SC -> exhibitorDao.getIndustryInExhibitorsSC(id)
        LANG_EN -> exhibitorDao.getIndustryInExhibitorsEN(id)
        else -> exhibitorDao.getIndustryInExhibitorsTC(id)
    }


    /*  ==========  Application =============  */
    fun getApplications(companyID: String) = run {
        when (getCurrLanguage()) {
            LANG_EN -> applicationDao.getApplicationEN(companyID)
            LANG_SC -> applicationDao.getApplicationSC(companyID)
            else -> applicationDao.getApplicationTC(companyID)
        }
    }

    fun getAllApplications() = run {
        when (getCurrLanguage()) {
            LANG_EN -> applicationDao.getAllApplicationEN()
            LANG_SC -> applicationDao.getAllApplicationSC()
            else -> applicationDao.getAllApplicationTC()
        }
    }


    /*  ==========  Industry  =============  */
    fun getIndustries(companyID: String) = run {
        when (getCurrLanguage()) {
            LANG_EN -> industryDao.getIndustriesEN(companyID)
            LANG_SC -> industryDao.getIndustriesSC(companyID)
            else -> industryDao.getIndustriesTC(companyID)
        }
    }

    fun getAllIndustries() = run {
        when (getCurrLanguage()) {
            LANG_EN -> industryDao.getAllIndustriesEN()
            LANG_SC -> industryDao.getAllIndustriesSC()
            else -> industryDao.getAllIndustriesTC()
        }
    }

    /*  ==========  HistoryExhibitor  =============  */
    fun insertToHistory(entity: ExhibitorHistory) = exhibitorDao.insertToHistory(entity)

    companion object {
        @Volatile
        private var instance: ExhibitorRepository? = null

        fun getInstance(
            exhibitorDao: ExhibitorDao,
            industryDao: IndustryDao,
            applicationDao: ApplicationDao
        ) =
            instance ?: synchronized(this) {
                instance ?: ExhibitorRepository(
                    exhibitorDao,
                    industryDao,
                    applicationDao
                ).also { instance = it }
            }
    }

}