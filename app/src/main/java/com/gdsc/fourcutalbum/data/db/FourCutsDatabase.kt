package com.gdsc.fourcutalbum.data.db

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

        // Google에서는 ALTER TABLE보다 DROP&CREATE 추천
        // 단순 컬럼 추가라 자동 Migration써도 될듯 하지만...? 일단 수동으로 작성
        // SQLite :  NULL, INTEGER(->int가능), REAL, TEXT(->VARCHAR가능), BLOB
        /*
        *   처음 migration 시도 시
        *   photo column not null여부가 달라서 실패: true --> false
        *   근데 column 추가만되고, not null 여부 업데이트는 어떻게 해야하는지...ㅜㅜ
        *   그래서 그냥 DROP&CREATE 하는게 나을지도....
        *
        */
//        private val MIGRATION_1_TO_2: Migration = object : Migration(1,2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // 변경하고자 하는 내용
//                database.run {
//                   execSQL(
//                        "CREATE TABLE IF NOT EXISTS new_fourcuts (" +
//                                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0, " +
//                                "title TEXT, " +
//                                "photo TEXT, " +
//                                "friends TEXT, " +
//                                "place TEXT, " +
//                                "public_yn TEXT NOT NULL DEFAULT 'N', " +
//                                "people TEXT, " +
//                                "hashtag TEXT)"
//                    );
//                    execSQL("INSERT INTO new_fourcuts (id, title, photo, friends, place, public_yn, people, hashtag) " +
//                            "SELECT id, title, photo, friends, place, public_yn, people, hashtag FROM fourcuts");
//                    execSQL("DROP TABLE fourcuts");
//                    execSQL("ALTER TABLE new_fourcuts RENAME TO fourcuts");
//                }
//            }
//        }


        private fun buildDatabase(context: Context): FourCutsDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                FourCutsDatabase::class.java,
                "my-fourcuts"
            ).build()
    //.addMigrations(MIGRATION_1_TO_2)
        fun getInstance(context: Context) : FourCutsDatabase =
            INSTANCE ?: synchronized(this){
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
    }
}