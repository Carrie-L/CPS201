package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.SeminarRepository

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class SeminarViewModelFactory(private val seminarRepository: SeminarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SeminarViewModel::class.java))
            return SeminarViewModel(seminarRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}