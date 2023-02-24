package com.gdsc.fourcutalbum.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gdsc.fourcutalbum.data.repository.FourCutsRepository

class MainViewModelProviderFactory(
    private val fourCutsRepository: FourCutsRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(fourCutsRepository) as T
        }
        throw IllegalArgumentException("viewModel class not found")
    }
}