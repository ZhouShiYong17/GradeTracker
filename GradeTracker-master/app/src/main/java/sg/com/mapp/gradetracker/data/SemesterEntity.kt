package sg.com.mapp.gradetracker.data

import android.arch.persistence.room.Entity
import kotlin.math.roundToInt

@Entity(tableName = "Semester",
    primaryKeys = ["semesterNo"])
data class SemesterEntity(
    val semesterNo: Int,
    var target: Float?
) {
    val year : Int
        get() = (semesterNo/ 2f).roundToInt()
    val semester : Int
        get() = 2 - (semesterNo % 2)

    fun succ(): Int = semesterNo + 1
}