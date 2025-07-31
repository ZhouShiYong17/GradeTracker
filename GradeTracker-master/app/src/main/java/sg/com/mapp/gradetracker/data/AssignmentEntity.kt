package sg.com.mapp.gradetracker.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Ignore
import java.util.*

@Entity(tableName = "Assignment",
    primaryKeys = ["semesterNo", "assignmentNo"], // moduleId is not included because it should not be considered in the sequence.
    foreignKeys = [
        ForeignKey(entity = ModuleEntity::class,
            parentColumns = ["semesterNo", "moduleId"],
            childColumns = ["semesterNo", "moduleId"],
            onDelete = CASCADE),
        ForeignKey(entity = SemesterEntity::class,
            parentColumns = ["semesterNo"],
            childColumns = ["semesterNo"],
            onDelete = CASCADE)
    ]
)
data class AssignmentEntity(val assignmentNo: Int,
                            val semesterNo: Int,
                            val moduleId: Int,
                            var shortName: String,
                            var longName: String,
                            var weightage: Float,
                            var maxScore: Float,
                            var target: Float?,
                            var score: Float?,
                            val creationDate: Date) {

    val scoreAsPercentage
        get() = score?.div(maxScore) ?.times(100)

    val targetAsPercentage
        get() = target?.div(maxScore)?.times(100)

}