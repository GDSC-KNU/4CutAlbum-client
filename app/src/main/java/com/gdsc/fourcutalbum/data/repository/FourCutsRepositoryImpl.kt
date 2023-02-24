package com.gdsc.fourcutalbum.data.repository

import android.net.Uri
import com.gdsc.fourcutalbum.data.db.FourCutsDatabase
import com.gdsc.fourcutalbum.data.model.FourCuts
import kotlinx.coroutines.flow.Flow

class FourCutsRepositoryImpl(
    private val db: FourCutsDatabase
) : FourCutsRepository {
    override suspend fun insertFourCuts(fourCuts: FourCuts) {
        db.fourCutsDao().insertFourCuts(fourCuts)
    }

    override suspend fun deleteFourCuts(fourCuts: FourCuts) {
        db.fourCutsDao().deleteFourCuts(fourCuts)
    }

    override fun deleteFourCutsWithId(id:Int) {
        db.fourCutsDao().deleteFourCutsWithId(id)
    }

    override suspend fun updateFourCuts(title: String?, photo: Uri, friends: List<String>?, place: String?, comment: String?, id: Int) {
        db.fourCutsDao().updateFourCuts(title, photo, friends, place, comment, id)
    }

    override fun getFourCuts(): Flow<List<FourCuts>> {
        return db.fourCutsDao().getFourCuts()
    }

    override fun getFourCutsWithId(id: Int): Flow<FourCuts> {
        return db.fourCutsDao().getFourCutsWithId(id)
    }

    override fun searchFourCuts(search: String): Flow<List<FourCuts>> {
        return db.fourCutsDao().searchFourCuts(search)
    }


}