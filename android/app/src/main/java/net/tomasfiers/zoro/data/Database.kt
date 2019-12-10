package net.tomasfiers.zoro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import net.tomasfiers.zoro.data.entities.*
import net.tomasfiers.zoro.data.entities.Collection
import org.threeten.bp.OffsetDateTime

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
    version = 9,
    exportSchema = false
)
@TypeConverters(DBTypeConverters::class)
abstract class ZoroDatabase : RoomDatabase() {

    abstract val keyValPair: KeyValPairDao
    abstract val schema: SchemaDao
    abstract val collection: CollectionDao
    abstract val item: ItemDao

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
    @TypeConverter
    fun toOffsetDateTime(value: String): OffsetDateTime = OffsetDateTime.parse(value)

    @TypeConverter
    fun fromOffsetDateTime(dateTime: OffsetDateTime): String = dateTime.toString()
}
