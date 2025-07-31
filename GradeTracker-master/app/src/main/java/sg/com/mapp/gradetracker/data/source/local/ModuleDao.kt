package sg.com.mapp.gradetracker.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.ModuleEntity

@Dao
interface ModuleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun newModule(module: ModuleEntity): Long

    @Query("SELECT * FROM Module WHERE semesterNo = :semesterNo AND moduleId = :moduleId")
    fun getModule(semesterNo: Int, moduleId: Int): ModuleEntity

    @Query("SELECT * FROM Module ORDER BY semesterNo DESC")
    fun getAllModules(): List<ModuleEntity>

    @Query("SELECT * FROM Assignment WHERE semesterNo = :semesterNo AND moduleId = :moduleId")
    fun getAssignmentsInModule(semesterNo: Int, moduleId: Int): List<AssignmentEntity>
    //fun getAssignmentsInModule(module: ModuleEntity): List<AssignmentEntity> = getAssignmentsInModule(module.semesterNo, module.moduleId)

    @Query("SELECT MAX(moduleId) FROM Module WHERE semesterNo = :semesterNo")
    fun getHighestId(semesterNo: Int): Int

    @Update
    fun updateModule(module: ModuleEntity)

    @Delete
    fun deleteModule(module: ModuleEntity)
}