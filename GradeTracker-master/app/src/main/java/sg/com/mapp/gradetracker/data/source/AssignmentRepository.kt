package sg.com.mapp.gradetracker.data.source

import android.content.Context
import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.source.local.AppDatabase
import sg.com.mapp.gradetracker.exts.SingletonHolder
import java.util.concurrent.Executors

class AssignmentRepository private constructor(private val database: AppDatabase) {
    private val executor = Executors.newSingleThreadExecutor()

    private val assignmentIndexed: Map<Int, AssignmentCollection> by lazy {
        SampleData
            .sampleAssignments
            .groupBy { it.semesterNo }
            .mapValues{ (_, list) -> list.associateBy { it.assignmentNo } } //database.assignmentDao().getAllAssignments()
    }

    val size
        get() = assignmentIndexed.size

    var assignments: List<AssignmentEntity> = listOf()
        get() = assignmentIndexed.flatMap { (_, m) -> m.values }

    operator fun get(semesterNo: Int): AssignmentCollection = assignmentIndexed[semesterNo] ?: emptyMap()

    operator fun get(semesterNo: Int, assignmentNo: Int): AssignmentEntity?
        = assignmentIndexed[semesterNo]?.get(assignmentNo)

    operator fun get(semesterNo: Int, key: Int, type: Key = Key.MODULE): AssignmentCollection {
        return when(type) {
            Key.MODULE -> assignmentIndexed[semesterNo]?.filter { (_, a) -> a.moduleId == key } ?: emptyMap()
            Key.ASSIGNMENT -> {
                val asn = get(semesterNo, key)
                asn?.let { mapOf(it.semesterNo to it) } ?: emptyMap()
            }
        }
    }

    fun insert(assignmentEntity: AssignmentEntity) {
        executor.execute {
            database.assignmentDao().newAssignment(assignmentEntity)
        }
    }

    /**
     * Overwrites assignmentNo with a number that is guaranteed to be unique before inserting
     * the new no is returned in a callback
     */
    fun insertWithUniqueId(assignmentEntity: AssignmentEntity, callback: ((Int) -> Unit)? = null) {
        executor.execute {
            database.runInTransaction {
                with(database.assignmentDao()) {
                    val newId = getHighestId(assignmentEntity.semesterNo) + 1
                    newAssignment(assignmentEntity.copy(assignmentNo = newId))
                    callback?.invoke(newId)
                }
            }
        }
    }

    fun update(assignmentEntity: AssignmentEntity) {
        executor.execute {
            database.assignmentDao().updateAssignment(assignmentEntity)
        }
    }

    fun delete(assignmentEntity: AssignmentEntity) {
        executor.execute {
            database.assignmentDao().deleteAssignment(assignmentEntity)
        }
    }

    companion object : SingletonHolder<AssignmentRepository, Context>({
        AssignmentRepository(AppDatabase.getInstance(it))
    })

    enum class Key {
        MODULE,
        ASSIGNMENT
    }
}

typealias AssignmentCollection = Map<Int, AssignmentEntity>
