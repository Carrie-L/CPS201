package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.ExhibitorDao
import com.adsale.chinaplas.data.dao.HtmlTextRepository
import com.adsale.chinaplas.data.dao.NewtechDao
import com.adsale.chinaplas.data.dao.SeminarDao

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class GlobalSearchViewModelFactory(private val htmlTextRepository: HtmlTextRepository,
                                   private val exhibitorDao: ExhibitorDao,
                                   private val newtechDao: NewtechDao,
                                   private val seminarDao: SeminarDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GlobalViewModel::class.java))
            return GlobalViewModel(htmlTextRepository, exhibitorDao, newtechDao, seminarDao) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}