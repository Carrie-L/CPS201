package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.data.dao.MainIconRepository

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class ExhibitorDtlViewModelFactory(private val exhibitorRepository: ExhibitorRepository, private val companyID: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExhibitorDtlViewModel::class.java))
            return ExhibitorDtlViewModel(exhibitorRepository,companyID) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}