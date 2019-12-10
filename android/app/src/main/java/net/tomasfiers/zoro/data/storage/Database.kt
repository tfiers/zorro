package net.tomasfiers.zoro.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import net.tomasfiers.zoro.data.entities.*
import net.tomasfiers.zoro.data.entities.Collection
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

@Database(
    entities = [
        KeyValPair::class,
        Collection::class,
        Item::class,
        ItemType::class,
        Field::class,
        CreatorType::class,
        ItemDataValue::class,
        Creator::class,
        ItemCollectionAssociation::class,
        ItemItemDataValueAssociation::class,
        ItemCreatorAssociation::class,
        ItemTypeFieldAssociation::class,
        ItemTypeCreatorTypeAssociation::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(DBTypeConverters::class)
abstract class ZoroDatabase : RoomDatabase() {

    abstract val keyValDao: KeyValDao
    abstract val schemaDAO: SchemaDAO
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

@Suppress("unused")
class DBTypeConverters {
    private val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    fun toOffsetDateTime(value: String): OffsetDateTime =
        dateTimeFormatter.parse(value, OffsetDateTime::from)

    @TypeConverter
    fun fromOffsetDateTime(dateTime: OffsetDateTime): String = dateTime.format(dateTimeFormatter)
}
