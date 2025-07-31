package sg.com.mapp.gradetracker.util

import sg.com.mapp.gradetracker.data.source.AssignmentRepository
import sg.com.mapp.gradetracker.data.source.GpaUpdateRepository
import sg.com.mapp.gradetracker.data.source.ModuleRepository
import sg.com.mapp.gradetracker.data.source.SemesterRepository

object Registry {
    lateinit var semesterRepo: SemesterRepository
    lateinit var moduleRepo: ModuleRepository
    lateinit var assignmentRepo: AssignmentRepository
    lateinit var gpaUpdateRepo: GpaUpdateRepository
}