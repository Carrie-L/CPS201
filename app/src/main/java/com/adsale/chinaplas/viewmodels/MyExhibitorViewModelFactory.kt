package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.ExhibitorRepository

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class MyExhibitorViewModelFactory(private val exhibitorRepository: ExhibitorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyExhibitorViewModel::class.java))
            return MyExhibitorViewModel(exhibitorRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}