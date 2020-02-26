package com.adsale.chinaplas.ui.newtech

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.NewtechRepository

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class NewtechFilterViewModelFactory(private val newtechRepository: NewtechRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewtechFilterViewModel::class.java))
            return NewtechFilterViewModel(newtechRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}