// Data acces objects
package net.tomasfiers.zoro.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface CollectionDAO {
    @Insert(onConflict = REPLACE)
    suspend fun insert(collection: Collection)

    @Query("select * from Collection where id = :id")
    suspend fun get(id: String): Collection

    // LiveData queries are automatically executed off the main thread by Room - so we don't mark
    // 'em as async.
    // Also, a gotcha: in SQL you cannot use `= null`, only `is null`.
    @Query("select * from Collection where (parentId = :parentId) or (:parentId is null and parentId is null)")
    fun getChildren(parentId: String?): LiveData<List<Collection>>
}
