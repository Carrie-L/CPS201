package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.*

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class ExhibitorHistoryViewModelFactory(
    private val exhibitorDao: ExhibitorDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExhibitorHistoryViewModel::class.java))
            return ExhibitorHistoryViewModel(exhibitorDao) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}