package com.gdsc.fourcutalbum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdsc.fourcutalbum.data.repository.FourCutsRepository

class FourCutsViewModelProviderFactory(
    private val fourCutsRepository: FourCutsRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FourCutsViewModel::class.java)){
            return FourCutsViewModel(fourCutsRepository) as T
        }
        throw IllegalArgumentException("viewModel class not found")
    }
}