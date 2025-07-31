package sg.com.mapp.gradetracker.data.source

import android.content.Context
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.source.local.AppDatabase
import sg.com.mapp.gradetracker.exts.SingletonHolder
import java.util.concurrent.Executors

class ModuleRepository private constructor(private val database: AppDatabase) {
    private val executor = Executors.newSingleThreadExecutor()

    private val moduleIndexed: Map<Int, ModuleCollection> by lazy {
        SampleData.sampleModules
        //database.moduleDao()
         //   .getAllModules()
            .groupBy { it.semesterNo }
            .mapValues{ (_, list) -> list.associateBy { it.moduleId } }
    }

    val size
        get() = moduleIndexed.size

    var modules: List<ModuleEntity> = listOf()
        get() = moduleIndexed.flatMap { (_, m) -> m.values }

    operator fun get(semesterNo: Int): ModuleCollection = moduleIndexed[semesterNo] ?: emptyMap()

    operator fun get(semesterNo: Int, moduleId: Int): ModuleEntity? = moduleIndexed[semesterNo]?.get(moduleId)

    fun insert(module: ModuleEntity) {
        executor.execute {
            database.moduleDao().newModule(module)
        }
    }

    /**
     * Overwrites moduleId with a number that is guaranteed to be unique before inserting
     * The new id is returned in the callback
     */
    fun insertWithUniqueId(module: ModuleEntity, callback: ((Int) -> Unit)? = null) {
        executor.execute {
            database.runInTransaction {
                with(database.moduleDao()) {
                    val newId = getHighestId(module.semesterNo) + 1
                    newModule(module.copy(moduleId = newId))
                    callback?.invoke(newId)
                }
            }
        }
    }

    fun update(module: ModuleEntity) {
        executor.execute {
            database.moduleDao().updateModule(module)
        }
    }

    fun delete(module: ModuleEntity) {
        executor.execute {
            database.moduleDao().deleteModule(module)
        }
    }

    companion object : SingletonHolder<ModuleRepository, Context>({
        ModuleRepository(AppDatabase.getInstance(it))
    })
}

typealias ModuleCollection = Map<Int, ModuleEntity>