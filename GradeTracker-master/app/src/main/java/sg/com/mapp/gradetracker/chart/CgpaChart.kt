package sg.com.mapp.gradetracker.chart

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import sg.com.mapp.gradetracker.R
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.util.Grade
import sg.com.mapp.gradetracker.util.Registry.semesterRepo
import sg.com.mapp.gradetracker.util.Statistics
import sg.com.mapp.gradetracker.util.assignments
import sg.com.mapp.gradetracker.util.cgpa
import sg.com.mapp.gradetracker.util.semester

class CgpaChart {
    data class Config(val semesterFormat: (Int, Int) -> String,
                      val targetFormat: (Float) -> String,
                      val typeface: Typeface,
                      val fontSize: Float,
                      val axisTextColor: Int,
                      val targetColor: Int,
                      val lineColor: Int,
                      val targetLineWidth: Float,
                      val lineWidth: Float,
                      val highlightColor: Int,
                      val circleRadius: Float,
                      val circleHoleRadius: Float,
                      val circleColor: Int,
                      val gradeMarkerColor: Int,
                      val bottomOffset: Float,
                      val markerLayout: Int,
                      val gradeLineWidth: Float,
                      val gradeTextColor: Int
    )

    companion object {
        @JvmStatic
        fun generateDefaultConfig(context: Context): Config {
            return Config(semesterFormat = { year, sem -> context.getString(R.string.format_semester_short, year, sem) },
                targetFormat = { value ->  context.getString(R.string.format_chart_target, value) },
                typeface = ResourcesCompat.getFont(context, R.font.fira_sans_condensed)!!,
                fontSize = 12f,
                axisTextColor = Color.WHITE,
                targetColor = ContextCompat.getColor(context, R.color.colorSecondary),
                lineColor = ContextCompat.getColor(context, R.color.colorTertiary),
                targetLineWidth = 2f,
                highlightColor = ContextCompat.getColor(context, R.color.colorSecondary),
                circleRadius = 8f,
                circleHoleRadius = 4f,
                circleColor = ContextCompat.getColor(context, R.color.colorTertiary),
                gradeMarkerColor = ContextCompat.getColor(context, R.color.colorTag),
                bottomOffset = 16f,
                lineWidth = 4f,
                markerLayout = R.layout.item_cgpa,
                gradeLineWidth = 0.5f,
                gradeTextColor = ContextCompat.getColor(context, R.color.colorTertiary)
            )
        }

        private fun determineAxisMinimum(y: List<Float>): Float
            = when(y.min() ?: 0f) {
                in 3..4 -> 3f
                in 2..4 -> 2f
                in 1..4 -> 1f
                else -> 0f
            }

        @JvmStatic
        fun generateDenseConfig(context: Context): Config
            = generateDefaultConfig(context).copy(circleRadius = 4f, circleHoleRadius = 1f)

        private fun applySettings(config: Config, lineChart: LineChart,x: List<String>, y: List<Float>) {
            with(lineChart) {
                description = Description().apply { text = "" }
                legend.isEnabled = false
                isDoubleTapToZoomEnabled = false
                isScaleXEnabled = false
                extraBottomOffset = config.bottomOffset
            }

            with(lineChart.xAxis) {
                removeAllLimitLines()
                axisMinimum = 0f
                axisMaximum = y.size.toFloat() - 1
                typeface = config.typeface
                textSize = config.fontSize
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textColor = config.axisTextColor
                valueFormatter = IAxisValueFormatter { value: Float, _: AxisBase ->
                    if (value.toInt() in 0 until x.size) x[value.toInt()] else ""
                }
                setDrawGridLines(false)
            }

            with(lineChart.axisLeft) {
                removeAllLimitLines()
                axisMinimum = determineAxisMinimum(y)
                axisMaximum = 4.0f
                typeface = config.typeface
                textColor = config.axisTextColor
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            lineChart.axisRight.isEnabled = false
        }

        @JvmStatic
        fun plotTargetLine(config: Config, lineChart: LineChart, target: Float) {
            lineChart.axisLeft.addLimitLine(LimitLine(target, config.targetFormat(target)).apply {
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                typeface = config.typeface
                textSize = config.fontSize
                lineWidth = config.targetLineWidth
                textColor = config.targetColor
                lineColor = config.targetColor
                enableDashedLine(40f, 40f, 0f)
            })
        }

        @JvmStatic
        fun basicPlot(config: Config, lineChart: LineChart, x: List<String>, y: List<Float>) {
            applySettings(config, lineChart, x, y)
            val values: List<Entry> = y.mapIndexed { i, it -> Entry(i.toFloat(), it) }

            val setCGPA: LineDataSet = LineDataSet(values, "CGPA").apply {
                // valueTextSize = 14f
                lineWidth = config.lineWidth
                circleRadius = config.circleRadius
                circleHoleRadius = config.circleHoleRadius
                axisDependency = YAxis.AxisDependency.LEFT
                setDrawValues(false)
                setDrawHorizontalHighlightIndicator(false)
                color = config.lineColor
                setCircleColor(config.circleColor)
            }

            val lineData: LineData = LineData(setCGPA)

            with(lineChart) {
                this.data = lineData
                invalidate()
            }
        }

        fun enableMarker(config: Config, lineChart: LineChart, context: Context) {
            lineChart.marker = CgpaMarkerView(context, config.markerLayout)
        }

        @JvmStatic
        fun plotOverview(config: Config, lineChart: LineChart, context: Context) {
            val semesters = semesterRepo.semesters.sortedBy { it.semesterNo }
            val x = semesters.map { config.semesterFormat(it.year, it.semester) }.toMutableList().apply { add(0, "Start") } as List<String>
            val y = semesters.map { it.cgpa }.toMutableList().apply { add(0, 4.0f) } as List<Float>

            basicPlot(config, lineChart, x, y)
            enableMarker(config, lineChart, context)

            // Plot target
            semesters.findLast { it.target != null }?.let {
                plotTargetLine(config, lineChart, it.target!!)
            }
        }

        fun plotModuleGradeChangeMarkers(config: Config, lineChart: LineChart, x: List<Int>, y: List<Grade>) {
            for (i in 0 until x.size) {
                lineChart.xAxis.addLimitLine(LimitLine(x[i].toFloat(), y[i].toString()).apply {
                    labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                    textSize = config.fontSize
                    lineWidth = config.gradeLineWidth
                    textColor = config.gradeTextColor
                    lineColor = config.gradeMarkerColor
                    enableDashedLine(64f, 36f, 0f)
                })
            }
        }

        @JvmStatic
        fun highlightModule(config: Config, lineChart: LineChart, context: Context, moduleEntity: ModuleEntity) {
            val points = Statistics.compute().filter { it.semesterNo == moduleEntity.semesterNo }
            val x = points
                .map {
                    if (it.changed && it.moduleId == moduleEntity.moduleId) {
                        moduleEntity.assignments[it.assignmentNo]?.shortName ?: "" }
                    else ""
                }
                .toMutableList()
                .apply {
                    add(0, config.semesterFormat(moduleEntity.semester.year, moduleEntity.semester.semester))
                } as List<String>
            val y = points
                .map { it.gpa }
                .toMutableList()
                .apply {
                    add(0, findStartingGpaFor(moduleEntity.semester))
                } as List<Float>

            basicPlot(config, lineChart, x, y)
            enableMarker(config, lineChart, context)

            lineChart.isScaleXEnabled = true

            val changes = points
                .mapIndexed { i, it -> i + 1 to it } // + 1 to account for injected point
                .filter { it.second.changed && (it.second.moduleId == moduleEntity.moduleId) }
            val changes_x = changes
                .map { it.first }
            val changes_y = changes
                .map { it.second.moduleGradeAfter }

            plotModuleGradeChangeMarkers(config, lineChart, changes_x, changes_y)
        }

        private fun findStartingGpaFor(semester: SemesterEntity): Float {
            return if (semester.semesterNo == 1) 4.0f
                else semesterRepo[semester.semesterNo - 1]?.cgpa!!
        }
    }

    class CgpaMarkerView(ctx: Context, layoutResource : Int) : MarkerView(ctx, layoutResource) {
        private val text: TextView = findViewById(R.id.dynCGPA)

        private var mOffset = MPPointF((-width / 2f), -height.toFloat())

        override fun refreshContent(e: Entry?, highlight: Highlight?) {
            e?.let {
                text.text = "%.2f".format(it.y)
                mOffset = MPPointF(-width.toFloat() - 12f, 12f + if (it.y >= 2.0f) 0f else -height.toFloat())
            }
            super.refreshContent(e, highlight)
        }

        override fun getOffset(): MPPointF = mOffset

    }
}