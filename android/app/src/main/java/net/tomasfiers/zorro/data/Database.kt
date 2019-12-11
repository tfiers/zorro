package net.tomasfiers.zorro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import net.tomasfiers.zorro.data.entities.*
import net.tomasfiers.zorro.data.entities.Collection
import org.threeten.bp.OffsetDateTime

@Database(
    entities = [
        KeyValPair::class,
        Collection::class,
        Item::class,
        ItemFieldValue::class,
        Creator::class,
        ItemType::class,
        Field::class,
        CreatorType::class,
        ItemCollectionAssociation::class,
        ItemTypeFieldAssociation::class,
        ItemTypeCreatorTypeAssociation::class
    ],
    version = 12,
    exportSchema = false
)
@TypeConverters(DBTypeConverters::class)
abstract class ZorroDatabase : RoomDatabase() {

    abstract val keyValPair: KeyValPairDao
    abstract val schema: SchemaDao
    abstract val collection: CollectionDao
    abstract val item: ItemDao

    // Singleton
    companion object {
        // Never cache: always read/write from main memory
        @Volatile
        private var database: ZorroDatabase? = null

        fun getDatabase(context: Context): ZorroDatabase {
            // Only have one execution thread run this block concurrently, to avoid duplicate
            // database creation.
            synchronized(this) {
                if (database == null) {
                    database = Room.databaseBuilder(
                        context.applicationContext,
                        ZorroDatabase::class.java,
                        "zorro_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return database as ZorroDatabase
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
