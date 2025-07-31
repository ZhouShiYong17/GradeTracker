package sg.com.mapp.gradetracker.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import sg.com.mapp.gradetracker.util.Grade

@Entity(tableName = "Module",
    primaryKeys = ["semesterNo", "moduleId"],
    foreignKeys = [
        ForeignKey(entity = SemesterEntity::class,
            parentColumns = ["semesterNo"],
            childColumns = ["semesterNo"],
            onDelete = CASCADE)
    ]
)
data class ModuleEntity(val moduleId: Int,
                        val semesterNo: Int,
                        val moduleCode: String,
                        var shortName: String,
                        var longName: String,
                        var creditUnits: Int,
                        var target: Grade?)