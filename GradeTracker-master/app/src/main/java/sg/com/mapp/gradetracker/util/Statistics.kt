package sg.com.mapp.gradetracker.util

import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.GpaUpdateEntity
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.util.Registry.gpaUpdateRepo
import sg.com.mapp.gradetracker.util.Registry.moduleRepo
import sg.com.mapp.gradetracker.util.Registry.semesterRepo

object Statistics {
    private var cache: List<GpaUpdateEntity>? = null
    private val semCache = mutableMapOf<Int, Float>()
    private val moduleCache = mutableMapOf<Pair<Int,Int>, Grade>()

    fun getAssignmentStatistics(semesterNo: Int, assignmentNo: Int): Pair<Float, GpaUpdateEntity> {
        val _cache = cache ?: compute()
        val gpaUpdateEntity = _cache.find { it.semesterNo == semesterNo && it.assignmentNo == assignmentNo }

        if (gpaUpdateEntity == null)
            return 0f to GpaUpdateEntity(0,0,0,0, Grade.A, Grade.A, 4.0f)

        val assnIndx = _cache.indexOf(gpaUpdateEntity)
        if (assnIndx > 0) {
            val prevGpa = _cache[assnIndx - 1].gpa
            return (gpaUpdateEntity.gpa - prevGpa) to gpaUpdateEntity
        } else {
            return 0f to gpaUpdateEntity
        }
    }

    fun getSemesterCgpa(semesterNo: Int): Float {
        val fromCache = semCache[semesterNo]
        if (fromCache != null) return fromCache

        val _cache = cache ?: compute()
        var semNo = semesterNo
        while (semNo > 0) {
            val gpaUpdateEntity = _cache.findLast { it.semesterNo == semNo }
            if (gpaUpdateEntity == null) {
                semNo -= 1
                continue
            }
            semCache[semesterNo] = gpaUpdateEntity.gpa
            return gpaUpdateEntity.gpa
        }

        semCache[semesterNo] = 4.0f
        return 4.0f
    }

    fun getModuleGrade(semesterNo: Int, moduleId: Int): Grade {
        val fromCache = moduleCache[semesterNo to moduleId]
        if (fromCache != null) return fromCache

        val _cache = cache ?: compute()
        val gpaUpdateEntity = _cache.findLast { (it.semesterNo == semesterNo) && (it.moduleId == moduleId) }
        if (gpaUpdateEntity != null) {
            moduleCache[semesterNo to moduleId] = gpaUpdateEntity.moduleGradeAfter
            return gpaUpdateEntity.moduleGradeAfter
        }

        moduleCache[semesterNo to moduleId] = Grade.A
        return Grade.A
    }


    fun compute(): List<GpaUpdateEntity> {
        if (cache != null) return cache!!

        val updates = _compute()

        cache = updates
        return updates
    }

    fun save() {
        gpaUpdateRepo.gpaUpdates = cache ?: compute()
    }

    /* Functional Style */
    private fun _compute(): List<GpaUpdateEntity> {
        val getAssignments = { it: ModuleEntity ->
            it.assignments.values
                .filter { it.score != null }
                .sortedWith(compareBy({ it.creationDate }, { it.assignmentNo }))  // Very important
        }

        fun <E, R> List<E>.scanLeft(initial: R, transform: (R, E) -> R) : List<R>
            = fold(mutableListOf(initial)) { acc: MutableList<R>, element: E ->
            acc.add(transform(acc.last(), element))
            acc
        }

        val computeModuleGradeForEachAssignment = { it: List<AssignmentEntity> ->
            val moduleBeforeAfter = it
                .scanLeft(Score.INIT) { old, asn -> old + asn.toScore()!! }
                .zipWithNext { before, after -> before.toGrade() to after.toGrade() }

            it.zip(moduleBeforeAfter)
        }

        val assignmentWithModuleGradeBeforeAfter = moduleRepo.modules
            .asSequence()
            .map(getAssignments)
            .map(computeModuleGradeForEachAssignment)
            .flatten()
            .sortedWith(compareBy({ it.first.semesterNo }, { it.first.assignmentNo }))
            .toList()

        fun computeGPA(map: Map<ModuleEntity, Grade>): Float {
            val accum = map.map { (module, grade) -> module.creditUnits * grade.gradePoint }.sum()
            val total = map.map { it.key.creditUnits }.sum()
            return accum / total
        }

        val semesterModules = semesterRepo.semesters.map { it.modules.values }
        val currentSemModules = mutableMapOf<ModuleEntity, Grade>()
        val gpaList = assignmentWithModuleGradeBeforeAfter
            .groupBy { it.first.semesterNo }
        var result = mutableListOf<Float>()

        for (currentSem in 1..(semesterRepo.semesters.maxBy { it.semesterNo }?.semesterNo ?: 1)) {
            currentSemModules.putAll(semesterModules[currentSem - 1].associate { it to Grade.A })

            result.addAll(gpaList[currentSem]?.map { (asn, grades) ->
                val (_, after) = grades
                currentSemModules[asn.module] = after
                computeGPA(currentSemModules)
            } ?: emptyList())
        }

        return assignmentWithModuleGradeBeforeAfter
            .zip(result)
            .mapIndexed { i, it ->
                val (asnBefAft ,gpa) = it
                val (asn, befAft) = asnBefAft
                val (bef, aft) = befAft

                GpaUpdateEntity(i + 1, asn.semesterNo, asn.moduleId, asn.assignmentNo, bef, aft, gpa)
            }
    }

    fun invalidate() {
        cache = null
        semCache.clear()
        moduleCache.clear()
        compute()
    }
}

