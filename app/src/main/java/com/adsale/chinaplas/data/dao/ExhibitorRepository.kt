package com.adsale.chinaplas.data.dao

import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.getCurrLanguage

/**
 * Created by Carrie on 2020/1/3.
 */
class ExhibitorRepository private constructor(private val exhibitorDao: ExhibitorDao) {

    fun insertAll(list: List<Exhibitor>) = exhibitorDao.insertExhibitorAll(list)

    fun updateExhibitorItem(entity:Exhibitor)  = exhibitorDao.updateExhibitorItem(entity)

//    fun getExhibitorLUT() = exhibitorDao.getLastUpdateTime()

    fun getAllExhibitorList() = when (getCurrLanguage()) {
        LANG_SC -> exhibitorDao.getAllExhibitorsSC()
        LANG_EN -> exhibitorDao.getAllExhibitorsEN()
        else -> exhibitorDao.getAllExhibitorsTC()
    }


    fun getAllExhibitors() = exhibitorDao.getAllExhibitors()

    companion object {
        @Volatile
        private var instance: ExhibitorRepository? = null

        fun getInstance(exhibitorDao: ExhibitorDao) =
            instance ?: synchronized(this) {
                instance ?: ExhibitorRepository(exhibitorDao).also { instance = it }
            }
    }

}