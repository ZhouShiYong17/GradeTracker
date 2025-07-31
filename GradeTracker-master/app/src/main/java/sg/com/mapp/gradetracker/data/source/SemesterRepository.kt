package sg.com.mapp.gradetracker.data.source

import android.content.Context
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.data.source.local.AppDatabase
import sg.com.mapp.gradetracker.exts.SingletonHolder
import java.util.concurrent.Executors

class SemesterRepository private constructor(private val database: AppDatabase) {

    private val executor = Executors.newSingleThreadExecutor()

    private val semesterIndexed: Map<Int, SemesterEntity> by lazy {
        SampleData
            .sampleSemesters
            .associateBy { it.semesterNo } //database.semesterDao().getAllSemesters()
    }

    val size
        get() = semesterIndexed.size

    var semesters: List<SemesterEntity> = listOf()
        get() = semesterIndexed.values.toList()

    operator fun get(semesterNo: Int): SemesterEntity? = semesterIndexed[semesterNo]

    fun insert(semesterEntity: SemesterEntity) {
        executor.execute {
            database.semesterDao().newSemester(semesterEntity)
        }
    }

    /**
     * Overwrites semesterNo with a number that is guaranteed to be unique before inserting
     * the new no is returned in a callback
     */
    fun insertWithUniqueId(semesterEntity: SemesterEntity, callback: ((Int) -> Unit)? = null) {
        executor.execute {
            database.runInTransaction {
                with(database.semesterDao()) {
                    val newId = getHighestId() + 1
                    newSemester(semesterEntity.copy(semesterNo = newId))
                    callback?.invoke(newId)
                }
            }
        }
    }

    fun update(semesterEntity: SemesterEntity) {
        executor.execute {
            database.semesterDao().updateSemester(semesterEntity)
        }
    }

    fun delete(semesterEntity: SemesterEntity) {
        executor.execute {
            database.semesterDao().deleteSemester(semesterEntity)
        }
    }

    companion object : SingletonHolder<SemesterRepository, Context>({
        SemesterRepository(AppDatabase.getInstance(it))
    })
}