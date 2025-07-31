package sg.com.mapp.gradetracker.util

import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.data.source.AssignmentCollection
import sg.com.mapp.gradetracker.data.source.AssignmentRepository
import sg.com.mapp.gradetracker.data.source.ModuleCollection
import sg.com.mapp.gradetracker.util.Registry.assignmentRepo
import sg.com.mapp.gradetracker.util.Registry.moduleRepo
import sg.com.mapp.gradetracker.util.Registry.semesterRepo

/*
    AssignmentEntity
 */
val AssignmentEntity.semester : SemesterEntity
    get() = semesterRepo.get(semesterNo)!!
val AssignmentEntity.module : ModuleEntity
    get() = moduleRepo.get(semesterNo, moduleId)!!
fun AssignmentEntity.toScore() : Score? = score?.let { Score(it, maxScore, weightage) }
fun AssignmentEntity.hasScore(): Boolean = score != null

/*
    ModuleEntity
 */
val ModuleEntity.semester : SemesterEntity
    get() = semesterRepo.get(semesterNo)!!
val ModuleEntity.assignments : AssignmentCollection
    get() = assignmentRepo.get(semesterNo, moduleId, AssignmentRepository.Key.MODULE)
val ModuleEntity.size : Int
    get() = assignments.size
val ModuleEntity.grade : Grade
    get() = Statistics.getModuleGrade(semesterNo, moduleId)

/*
    SemesterEntity
 */
val SemesterEntity.modules: ModuleCollection
    get() = moduleRepo.get(semesterNo)
val SemesterEntity.assignments: AssignmentCollection
    get() = assignmentRepo.get(semesterNo)
val SemesterEntity.cgpa
    get() = Statistics.getSemesterCgpa(semesterNo)
val SemesterEntity.totalCreditUnits
    get() = modules.map { it.value.creditUnits }.sum()