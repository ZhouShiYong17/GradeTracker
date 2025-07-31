package sg.com.mapp.gradetracker.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Ignore
import sg.com.mapp.gradetracker.util.Grade

@Entity(tableName = "gpaCache",
    primaryKeys = ["updateId"],
    foreignKeys = [
        ForeignKey(entity = AssignmentEntity::class,
            parentColumns = ["semesterNo", "assignmentNo"],
            childColumns = ["semesterNo", "assignmentNo"]),
        ForeignKey(entity = ModuleEntity::class,
            parentColumns = ["semesterNo", "moduleId"],
            childColumns = ["semesterNo", "moduleId"]),
        ForeignKey(entity = SemesterEntity::class,
            parentColumns = ["semesterNo"],
            childColumns = ["semesterNo"])
    ])
data class GpaUpdateEntity(val updateId: Int,
                           val semesterNo: Int,
                           val moduleId: Int,
                           val assignmentNo: Int,
                           val moduleGradeBefore: Grade,
                           val moduleGradeAfter: Grade,
                           val gpa: Float) {
    @Ignore
    val changed : Boolean = moduleGradeBefore != moduleGradeAfter
}