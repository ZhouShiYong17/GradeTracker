package sg.com.mapp.gradetracker.viewmodel

import android.arch.lifecycle.ViewModel
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.util.Registry.semesterRepo
import sg.com.mapp.gradetracker.util.Statistics
import sg.com.mapp.gradetracker.util.modules

class SemesterViewModel : ViewModel() {
    lateinit var semesterEntity: SemesterEntity
    var year: Int = 0
    var semester: Int = 0

    fun init(semesterId: Int) {
        semesterEntity = semesterRepo[semesterId]!!
        year = semesterEntity.year
        semester = semesterEntity.semester
    }

    val totalCreditUnits: Int
        get() = semesterEntity.modules.values.map { it.creditUnits }.sum()

    val gpa: Float
        get() = Statistics.getSemesterCgpa(semesterEntity.semesterNo)
}