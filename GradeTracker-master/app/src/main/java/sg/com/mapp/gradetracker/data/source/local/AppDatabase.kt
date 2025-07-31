package sg.com.mapp.gradetracker.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.GpaUpdateEntity
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.exts.SingletonHolder

@Database(entities = [
    SemesterEntity::class,
    ModuleEntity::class,
    AssignmentEntity::class,
    GpaUpdateEntity::class
], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun semesterDao(): SemesterDao

    abstract fun moduleDao(): ModuleDao

    abstract fun assignmentDao(): AssignmentDao

    abstract fun gpaUpdateDao(): GpaUpdateDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext,
            AppDatabase::class.java, "database.db")
            .build()
    })

}

