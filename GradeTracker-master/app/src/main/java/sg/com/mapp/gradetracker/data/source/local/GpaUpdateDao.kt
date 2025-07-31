package sg.com.mapp.gradetracker.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import sg.com.mapp.gradetracker.data.GpaUpdateEntity

@Dao
interface GpaUpdateDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUpdate(update: GpaUpdateEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUpdates(updates: List<GpaUpdateEntity>): List<Long>

    @Query("SELECT * FROM gpaCache")
    fun loadUpdates(): List<GpaUpdateEntity>

    @Query("SELECT * FROM gpaCache WHERE semesterNo = :semesterNo")
    fun loadUpdate(semesterNo: Int): List<GpaUpdateEntity>
    //fun loadUpdate(semester: SemesterEntity): List<GpaUpdateEntity> = loadUpdate(semester.semesterNo)

    @Query("DELETE FROM gpaCache")
    fun invalidate()
}