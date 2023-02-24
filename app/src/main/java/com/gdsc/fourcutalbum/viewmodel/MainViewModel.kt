package com.gdsc.fourcutalbum.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdsc.fourcutalbum.data.model.FourCuts
import com.gdsc.fourcutalbum.data.repository.FourCutsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val fourCutsRepository: FourCutsRepository,
) : ViewModel(){

    // 변경가능한 Mutable 타입의 LiveData
    private val _searchWord = MutableLiveData<String>()

    // 무결성을 위한 Getter
    val searchWord : LiveData<String>
        get() = _searchWord

    // 초기값
    init{ _searchWord.value = "" }

    // Setter
    fun updateValue(type : String){
        _searchWord.value = type
    }


    // Room
    fun saveFourCuts(fourCuts: FourCuts) = viewModelScope.launch(Dispatchers.IO){
        fourCutsRepository.insertFourCuts(fourCuts)
    }

    fun deleteFourCuts(fourCuts: FourCuts) = viewModelScope.launch(Dispatchers.IO){
        fourCutsRepository.deleteFourCuts(fourCuts)
    }

    // TestActivity의 lifecycle과 동기화
    val getFourCuts: StateFlow<List<FourCuts>> = fourCutsRepository.getFourCuts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    fun getFourCutsWithId(id: Int): StateFlow<FourCuts> {
        return fourCutsRepository.getFourCutsWithId(id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FourCuts("", Uri.EMPTY, listOf(),"",""))
    }

    fun searchFourCuts(searchWord: String): StateFlow<List<FourCuts>> {
        return fourCutsRepository.searchFourCuts("%${searchWord}%").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())
    }


}