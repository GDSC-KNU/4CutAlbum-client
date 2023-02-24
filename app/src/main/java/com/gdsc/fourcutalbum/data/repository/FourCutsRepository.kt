package com.gdsc.fourcutalbum.data.repository

import android.net.Uri
import com.gdsc.fourcutalbum.data.model.FourCuts
import kotlinx.coroutines.flow.Flow

interface FourCutsRepository {

    // Room
    suspend fun insertFourCuts(fourCuts: FourCuts)

    suspend fun deleteFourCuts(fourCuts: FourCuts)

    fun deleteFourCutsWithId(id: Int)

    suspend fun updateFourCuts(title: String?, photo: Uri, friends: List<String>?, place: String?, comment: String?, id: Int)

    fun getFourCuts(): Flow<List<FourCuts>>

    fun getFourCutsWithId(id: Int): Flow<FourCuts>

    fun searchFourCuts(search: String) : Flow<List<FourCuts>>
}