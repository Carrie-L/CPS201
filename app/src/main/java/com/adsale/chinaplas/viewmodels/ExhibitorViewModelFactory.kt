package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.ui.view.RecyclerViewScrollTo

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class ExhibitorViewModelFactory(
    val app: Application,
    val exhibitorRepository: ExhibitorRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExhibitorViewModel::class.java))
            return ExhibitorViewModel(app, exhibitorRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}