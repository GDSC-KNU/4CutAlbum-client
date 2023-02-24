package com.gdsc.fourcutalbum.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gdsc.fourcutalbum.data.model.FourCuts

@Database(
    entities = [FourCuts::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(OrmConverter::class)
abstract class FourCutsDatabase : RoomDatabase(){

    abstract fun fourCutsDao() : FourCutsDao

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: FourCutsDatabase? = null

        private fun buildDatabase(context: Context): FourCutsDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                FourCutsDatabase::class.java,
                "my-fourcuts"
            ).build()

        fun getInstance(context: Context) : FourCutsDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
    }
}