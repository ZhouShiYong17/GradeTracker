package sg.com.mapp.gradetracker

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_view_module.*
import kotlinx.android.synthetic.main.content_view_module.*
import sg.com.mapp.gradetracker.util.Grade
import sg.com.mapp.gradetracker.util.Registry.moduleRepo
import sg.com.mapp.gradetracker.util.semester
import sg.com.mapp.gradetracker.util.totalCreditUnits
import java.util.*

class ViewModuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_module)
        setSupportActionBar(toolbar)
        /*
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_close)
        pie()

        val moduleId = 1
        val semesterID = 1
        val moduleEntity = moduleRepo[semesterID, moduleId]!!
        viewModuleDynModuleLongName.text = moduleEntity.longName
        viewModuleDynModuleShortName.text = moduleEntity.shortName
        viewModuleDynModuleSemester.text = moduleEntity.semester.toString()
        viewModuleDynCreditUnit.text = moduleEntity.creditUnits.toString()
        viewModuleDynTotalCredit.text = getString(R.string.format_score_out_of_maxScore, moduleEntity.creditUnits.toString(), moduleEntity.semester.totalCreditUnits.toString())
        //not really done yet
        viewModuleDynTargetGrade.text = Grade.F.name
        viewModuleDynTargetGradeDescriptors.text = Grade.F.description
        viewModuleDynModuleGrade.text = Grade.D_MINUS.name
        viewModuleDynModuleGradeDescriptor.text = Grade.D_MINUS.description
        //stats
        //stats
    }

    fun pie() {
        pieModuleGrade.setHoleRadius(80f)
        pieModuleGrade.setHoleColor(ColorTemplate.COLOR_NONE)
        pieModuleGrade.setDescription(Description().apply { text = "" })
        pieModuleGrade.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.fira_sans_condensed))
        pieModuleGrade.getLegend().setEnabled(false)
        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry(15f, "P1"))
        entries.add(PieEntry(10f, "P2"))
        entries.add(PieEntry(35f, "EXAM"))
        entries.add(PieEntry(25f, "Others"))
        entries.add(PieEntry(15f))

        val set = PieDataSet(entries, "Grade")

        set.setDrawValues(false)
        set.setColors(intArrayOf(R.color.colorQuaternary, R.color.colorTertiary, R.color.colorSecondary, R.color.colorQuaternary, R.color.colorTransparent), this)

        val data = PieData(set)
        pieModuleGrade.data = data
        pieModuleGrade.centerText = "A"
        pieModuleGrade.setCenterTextColor(Color.WHITE)
        pieModuleGrade.setCenterTextSize(50f)

        pieModuleGrade.invalidate()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true
    }

}
