package sg.com.mapp.gradetracker.data.source

import android.content.Context
import sg.com.mapp.gradetracker.data.GpaUpdateEntity
import sg.com.mapp.gradetracker.data.source.local.AppDatabase
import sg.com.mapp.gradetracker.exts.SingletonHolder
import java.util.concurrent.Executors

class GpaUpdateRepository private constructor(private val database: AppDatabase) {
    private val executor = Executors.newSingleThreadExecutor()

    var gpaUpdates: List<GpaUpdateEntity> = listOf()
        get() = database.gpaUpdateDao().loadUpdates()
        set(value) {
            database.gpaUpdateDao().invalidate()
            executor.execute {
                database.gpaUpdateDao().insertUpdates(value)
            }
            field = value
        }

    companion object : SingletonHolder<GpaUpdateRepository, Context>({
        GpaUpdateRepository(AppDatabase.getInstance(it))
    })
}