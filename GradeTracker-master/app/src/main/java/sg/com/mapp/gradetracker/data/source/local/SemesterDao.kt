package sg.com.mapp.gradetracker.data.source.local

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.SemesterEntity

@Dao
interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun newSemester(semester: SemesterEntity): Long

    @Query("SELECT * FROM Semester WHERE semesterNo = :semesterNo")
    fun getSemester(semesterNo: Int): LiveData<SemesterEntity>

    @Query("SELECT * FROM Module WHERE semesterNo = :semesterNo")
    fun getModulesInSemester(semesterNo: Int): LiveData<List<ModuleEntity>>
    //fun getModulesInSemester(semester: SemesterEntity): List<ModuleEntity> = getModulesInSemester(semester.semesterNo)

    @Query("SELECT * FROM Assignment WHERE semesterNo = :semesterNo ORDER BY assignmentNo DESC")
    fun getAssignmentsInSemester(semesterNo: Int): LiveData<List<AssignmentEntity>>
    //fun getAssignmentsInSemester(semester: SemesterEntity): List<AssignmentEntity> = getAssignmentsInSemester(semester.semesterNo)

    @Query("SELECT * FROM Semester")
    fun getAllSemesters(): LiveData<List<SemesterEntity>>

    @Query("SELECT MAX(semesterNo) FROM Semester")
    fun getHighestId(): Int

    @Update
    fun updateSemester(semester: SemesterEntity)

    @Delete
    fun deleteSemester(semester: SemesterEntity)
}