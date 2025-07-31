package sg.com.mapp.gradetracker.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import sg.com.mapp.gradetracker.data.AssignmentEntity

@Dao
interface AssignmentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun newAssignment(assignment: AssignmentEntity): Long

    @Query("SELECT * FROM Assignment WHERE semesterNo = :semesterNo AND assignmentNo = :assignmentNo")
    fun getAssignment(semesterNo: Int, assignmentNo: Int): AssignmentEntity

    @Query("SELECT * FROM Assignment ORDER BY semesterNo DESC, assignmentNo DESC")
    fun getAllAssignments(): List<AssignmentEntity>

    @Query("SELECT MAX(assignmentNo) FROM Assignment WHERE semesterNo = :semesterNo")
    fun getHighestId(semesterNo: Int): Int

    @Update
    fun updateAssignment(assignment: AssignmentEntity)

    @Delete
    fun deleteAssignment(assignment: AssignmentEntity)
}