package com.adsale.chinaplas.data.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.getCurrLanguage

class NewtechRepository private constructor(private val newtechDao: NewtechDao) {


    fun getAllProductInfoList(): MutableList<NewtechProductInfo> = run {
        when (getCurrLanguage()) {
            LANG_EN -> newtechDao.getAllProductInfoListEN()
            LANG_SC -> newtechDao.getAllProductInfoListSC()
            else -> newtechDao.getAllProductInfoListTC()
        }
    }

    fun getFilterSubList(type: String): MutableList<NewtechCategorySub> = run {
        when (getCurrLanguage()) {
            LANG_EN -> newtechDao.getFilterProductsEN(type)
            LANG_SC -> newtechDao.getFilterProductsSC(type)
            else -> newtechDao.getFilterProductsTC(type)
        }
    }

    fun getFilterResultList(sql: String): MutableList<NewtechProductInfo> = run {
        val query = SimpleSQLiteQuery(sql)
        newtechDao.getFilterList(query)
    }

    fun getItemInfo(rid: String): NewtechProductInfo = newtechDao.getItemInfo(rid)


    fun getFilterQuery(sql: String) {
        val query = SimpleSQLiteQuery(sql)
        newtechDao.getFilterList(query)
    }

    companion object {
        @Volatile
        private var instance: NewtechRepository? = null

        fun getInstance(newtechDao: NewtechDao) =
            instance ?: synchronized(this) {
                instance ?: NewtechRepository(newtechDao).also { instance = it }
            }
    }
}