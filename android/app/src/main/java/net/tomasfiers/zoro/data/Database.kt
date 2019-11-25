package net.tomasfiers.zoro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Collection::class], version = 1, exportSchema = false)
abstract class ZoroDatabase : RoomDatabase() {

    abstract val collectionDAO: CollectionDAO

    // Singleton
    companion object {
        // Never cache: always read/write from main memory
        @Volatile
        private var database: ZoroDatabase? = null

        fun getDatabase(context: Context): ZoroDatabase {
            // Only have one execution thread run this block concurrently, to avoid duplicate
            // database creation.
            synchronized(this) {
                if (database == null) {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        ZoroDatabase::class.java,
                        "zoro_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return database as ZoroDatabase
            }
        }
    }
}
