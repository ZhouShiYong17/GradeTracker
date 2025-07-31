package sg.com.mapp.gradetracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_view_assignment.*
import kotlinx.android.synthetic.main.content_view_assignment.*
import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.util.Registry.assignmentRepo
import sg.com.mapp.gradetracker.util.Statistics
import sg.com.mapp.gradetracker.util.module
import sg.com.mapp.gradetracker.util.semester
import java.util.*

class ViewAssignmentActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var assignmentEntity: AssignmentEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_assignment)
        setSupportActionBar(toolbar)

        fabAssignmentEdit.setOnClickListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_close)
        pie()

        val bundle = intent.extras
        val semesterNo = bundle.get("semesterNo") as Int
        val assignmentNo = bundle.get("assignmentNo") as Int
        assignmentEntity = assignmentRepo[semesterNo, assignmentNo]!!

        supportActionBar?.title = "${assignmentEntity.module.shortName}/${assignmentEntity.shortName}"

        assignmentEntity.apply {
            viewAssignDynAssignmentLongName.text = longName
            viewAssignDynAssignmentShortName.text = shortName
            viewAssignDynModuleName.text = "%s (%s)".format(module.longName, module.shortName)
            viewAssignDynSemester.text = getString(R.string.format_semester, semester.year, semester.semester)
            viewAssignDynAssignmentWeightagePercent.text = formatPercentage(weightage)
            viewAssignDynAssignmentWeightage.text = getString(R.string.format_score_out_of_maxScore, formatNullString(weightage), "100")
            viewAssignDynAssignmentTargetScorePercent.text = formatPercentage(targetAsPercentage)
            viewAssignDynAssignmentTargetScore.text = getString(R.string.format_score_out_of_maxScore, formatNullString(target), maxScore.toString())
            viewAssignDynAssignmentActualScorePercentage.text = formatPercentage(scoreAsPercentage)
            viewAssignDynAssignmentActualScore.text = getString(R.string.format_score_out_of_maxScore, formatNullString(score), maxScore.toString())

            viewAssignDynAssignmentStatistic.text = formatStatistic(assignmentEntity)
            viewAssignDynGradeReminder.text = "" // Not done yet
        }

    }

    private fun formatStatistic(assignmentEntity: AssignmentEntity): String {
        val (cgpa_change, gpaUpdateEntity) = Statistics.getAssignmentStatistics(assignmentEntity.semesterNo, assignmentEntity.assignmentNo)

        // Check whether there is a change in grades
        if (gpaUpdateEntity.changed) {
            // Check whether it is a rise or a drop
            val isRise = gpaUpdateEntity.moduleGradeAfter > gpaUpdateEntity.moduleGradeBefore
            val riseOrDrop = if (isRise) getString(R.string.placeholder_rise) else getString(R.string.placeholder_drop)
            return getString(R.string.format_assignment_statistic_changed,
                riseOrDrop,
                gpaUpdateEntity.moduleGradeBefore,
                gpaUpdateEntity.moduleGradeAfter,
                cgpa_change)
        } else {
            return getString(R.string.format_assignment_statistic_unchanged)
        }
    }

    /**
     * Changes a value like 0.93535 to "93.5%"
     */
    private fun formatPercentage(value: Float?): String = value?.let { "%.1f%%".format(it) } ?: "-"

    private fun formatNullString(value: Float?): String = value?.let { "%s".format(it) }?: "-"

    fun pie() {
        pieAssignmentScore.setHoleRadius(90f)
        pieAssignmentScore.setHoleColor(ColorTemplate.COLOR_NONE)
        pieAssignmentScore.setDescription(Description().apply { text = "" })
        pieAssignmentScore.setDrawEntryLabels(false)
        pieAssignmentScore.getLegend().setEnabled(false)

        val entries = ArrayList<PieEntry>()

        entries.add(PieEntry(60f))
        entries.add(PieEntry(40f))

        val set = PieDataSet(entries, "Assignment 1")

        set.setDrawValues(false)
        set.setColors(intArrayOf(R.color.colorQuaternary, R.color.colorTransparent), this)

        val data = PieData(set)
        pieAssignmentScore.data = data
        pieAssignmentScore.centerText = "60.0"
        pieAssignmentScore.setCenterTextColor(Color.WHITE)
        pieAssignmentScore.setCenterTextSize(50f)
        pieAssignmentScore.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.fira_sans_condensed))

        pieAssignmentScore.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.fabAssignmentEdit -> {
                    val intent = Intent(this, AddAssignmentActivity::class.java)
                    intent.putExtra("semesterNo", assignmentEntity.semesterNo)
                    intent.putExtra("assignmentNo", assignmentEntity.assignmentNo)
                    startActivity(intent)
                }
            }
        }
    }

}
