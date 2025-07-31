package sg.com.mapp.gradetracker

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.data.source.AssignmentRepository
import sg.com.mapp.gradetracker.data.source.GpaUpdateRepository
import sg.com.mapp.gradetracker.data.source.ModuleRepository
import sg.com.mapp.gradetracker.data.source.SampleData
import sg.com.mapp.gradetracker.data.source.SemesterRepository
import sg.com.mapp.gradetracker.data.source.local.AppDatabase
import sg.com.mapp.gradetracker.util.Registry
import sg.com.mapp.gradetracker.util.Statistics
import java.lang.IllegalStateException
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var pagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Registry.semesterRepo = SemesterRepository.getInstance(applicationContext)
        Registry.moduleRepo = ModuleRepository.getInstance(applicationContext)
        Registry.assignmentRepo = AssignmentRepository.getInstance(applicationContext)
        Registry.gpaUpdateRepo = GpaUpdateRepository.getInstance(applicationContext)

        checkFirstRun()

        pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        pager.adapter = pagerAdapter
    }

    private fun checkFirstRun() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (pref.getBoolean("firstRun", true)) {
            val database = AppDatabase.getInstance(this)
            Executors.newSingleThreadExecutor().execute {
                val isSuccess = database.runInTransaction (Callable {
                    for (semester in SampleData.sampleSemesters) {
                        database.semesterDao().newSemester(semester)
                        Log.d("Database", semester.semesterNo.toString())
                    }
                    for (module in SampleData.sampleModules) {
                        database.moduleDao().newModule(module)
                    }
                    for (assignment in SampleData.sampleAssignments) {
                        database.assignmentDao().newAssignment(assignment)
                    }

                    true
                })

                Log.d("Database finished", isSuccess.toString())

                Registry.semesterRepo.insertWithUniqueId(SemesterEntity(0, 3.99f))
                Statistics.invalidate()
                Statistics.save()
            }




            pref.edit().putBoolean("firstRun", false).apply()
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when(position) {
                0 -> return OverviewFragment()
                1 -> return TargetFragment()
                else -> throw IllegalStateException()
            }
        }

        override fun getCount() = 2
    }

}