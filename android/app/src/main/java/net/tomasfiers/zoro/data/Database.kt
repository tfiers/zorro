package net.tomasfiers.zoro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@Database(entities = [Collection::class], version = 3, exportSchema = false)
@TypeConverters(DBTypeConverters::class)
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

class DBTypeConverters {

    private val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String): OffsetDateTime =
        dateTimeFormatter.parse(value, OffsetDateTime::from)

    @TypeConverter
    fun fromOffsetDateTime(dateTime: OffsetDateTime): String = dateTime.format(dateTimeFormatter)
}
