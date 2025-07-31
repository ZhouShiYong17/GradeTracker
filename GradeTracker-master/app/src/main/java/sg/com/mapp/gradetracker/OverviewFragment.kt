package sg.com.mapp.gradetracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.component_toolbar.*
import kotlinx.android.synthetic.main.fragment_overview.*
import sg.com.mapp.gradetracker.chart.CgpaChart
import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.source.AssignmentCollection
import sg.com.mapp.gradetracker.data.source.ModuleCollection
import sg.com.mapp.gradetracker.util.Registry
import sg.com.mapp.gradetracker.util.Statistics
import sg.com.mapp.gradetracker.util.assignments
import sg.com.mapp.gradetracker.util.module
import sg.com.mapp.gradetracker.util.modules
import sg.com.mapp.gradetracker.viewmodel.ToolbarViewModel
import kotlin.math.max

class OverviewFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {
    private lateinit var toolbarVM : ToolbarViewModel
    private lateinit var moduleAdapter: ModuleAdapter
    private lateinit var assignmentAdapter: AssignmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
        setupBottomSheet()
        setupAdapters()
        setCurrentSemester(getCurrentSemester())
        registerListeners()
        CgpaChart.plotOverview(CgpaChart.generateDefaultConfig(requireContext()), dynGraphOverview, requireContext())
    }

    /**
     * Measures and sets the correct height of the bottom sheet
     */
    private fun setupBottomSheet() {
        val btmSht = BottomSheetBehavior.from(bottomSheet)

        val listener = object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                bottomSheet.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val baseHeight = graphContainer.height
                val toolbarHeight = toolbar_container.height
                val params = bottomSheet.layoutParams
                bottomSheet.layoutParams = params.apply {
                    height = baseHeight + toolbarHeight + resources.getDimensionPixelSize(R.dimen.bottomsheet_gap)
                }
                bottomSheet.invalidate()
            }
        }
        bottomSheet.viewTreeObserver.addOnGlobalLayoutListener(listener)
        btmSht.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fab -> {
                    chooseAddModuleOrAddAssignment()
                }
            }
        }
    }

    private fun chooseAddModuleOrAddAssignment() {
        AlertDialog
            .Builder(requireContext())
            .setItems(arrayOf("Add Assignment", "Add Module")) { dialog, which ->
                when(which) {
                    0 -> {
                        val intent = Intent(requireContext(), AddAssignmentActivity::class.java)
                        startActivity(intent)
                    }
                    1 -> {
                        val intent = Intent(requireContext(), AddModuleActivity::class.java)
                        startActivity(intent)
                    }
                }
            }.show()
    }

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.menu_overview)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    true
                }
            }
        }

        initToolbarVM()
    }

    // This function is responsible for setting up the event listeners for the toolbar via
    // as well as setting some default values for display purposes.
    private fun initToolbarVM() {
        toolbarVM = ViewModelProviders.of(this).get(ToolbarViewModel::class.java)

        val ctx = this

        toolbarVM.apply {
            // Event listeners which update the TextViews with the new data.
            currentGpa.observe(ctx, Observer { dynGPA.text = it })
            gpaType.observe(ctx, Observer { labelSemesterGPA.text = it })
            currentSemester.observe(ctx, Observer { dynSemester.text = it })
            contextualMessage.observe(ctx, Observer { ctx.contextualMessage.text = it })

            // Modifying the data here will cause the listeners to be fired which will update the TextViews.
            setGpa(Statistics.getSemesterCgpa(1))
            setGpaType(getString(R.string.label_cumulative_gpa))
            setCurrentSemester(context!!, Registry.semesterRepo[1]!!)
            setContextualMessage("Drag down to view modules")
        }
    }

    /**
     * Responsible for registering all the various listeners,
     * eg. listening for when user changes the current semester
     */
    private fun registerListeners() {
        fab.setOnClickListener(this)
        PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

    /**
     * Responsible for unregistering listeners
     */
    override fun onDetach() {
        super.onDetach()
        PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
    }


    private fun setCurrentSemester(semesterNo : Int) {
        val semester = Registry.semesterRepo[semesterNo]
        if (semester != null && context != null) {
            toolbarVM.setGpa(Statistics.getSemesterCgpa(semesterNo))
            toolbarVM.setCurrentSemester(requireContext(), semester)
            moduleAdapter.update(semester.modules)
            assignmentAdapter.update(semester.assignments)
            CgpaChart.plotOverview(CgpaChart.generateDefaultConfig(requireContext()), dynGraphOverview, requireContext())
        }
    }

    /**
     * Update itself when the user decides to change the current semester
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null) {
            if (key == SettingsActivity.KEY_SET_CURRENT_SEMESTER) {
                val semesterId = sharedPreferences
                    .getString(SettingsActivity.KEY_SET_CURRENT_SEMESTER, "1")
                    .toInt()

                setCurrentSemester(semesterId)
            }
        }
    }

    private fun setupAdapters() {
        moduleAdapter = ModuleAdapter(emptyMap())
        assignmentAdapter = AssignmentAdapter(emptyMap())

        with(dynModules) {
            setHasFixedSize(true)
            adapter = moduleAdapter
        }

        with(dynAssignments) {
            setHasFixedSize(true)
            adapter = assignmentAdapter
        }
    }

    private fun getCurrentSemester()
        = PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .getString(SettingsActivity.KEY_SET_CURRENT_SEMESTER, "1")
            .toInt()


    inner class AssignmentAdapter(private var assignmentsList : AssignmentCollection) : RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v : View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_assignment, parent, false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int = max(1, assignmentsList.size)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (assignmentsList.isEmpty()) {
                onEmpty(holder)
                return
            }

            // Do not use assignmentsList[position] as AssignmentCollection#get(assignmentNo)
            // assignmentNo != position (e.g. some assignments could have been deleted)
            assignmentsList.values.toList()[position]?.let { assignment ->
                holder.apply {
                    module.text = assignment.module.shortName
                    longName.text = assignment.longName
                    shortName.text = assignment.shortName
                    score.text = "%.1f".format(assignment.score)
                    menu.visibility = View.VISIBLE
                    menu.setOnClickListener { it ->
                        showPopupMenu(it, assignment)
                    }
                    itemView.setOnClickListener {  }
                }
            }
        }

        private fun onEmpty(holder: ViewHolder) {
            holder.apply {
                module.text = "+"
                longName.text = getString(R.string.label_create_new_assignment)
                shortName.text = getString(R.string.label_create_new_assignment_subtitle)
                score.text = ""
                menu.visibility = View.INVISIBLE
                itemView.setOnClickListener {
                    val intent = Intent(requireContext(), AddAssignmentActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        private fun showPopupMenu(v: View, assignment: AssignmentEntity) {
            val popup = PopupMenu(v.context, v)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.menu_view, popup.menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_view -> {
                        val intent = Intent(v.context, ViewAssignmentActivity::class.java)
                        intent.putExtra("semesterNo", assignment.semesterNo)
                        intent.putExtra("moduleId", assignment.moduleId)
                        intent.putExtra("assignmentNo", assignment.assignmentNo)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        internal fun update(newAssignmentsList: AssignmentCollection) {
            assignmentsList = newAssignmentsList
            notifyDataSetChanged()
        }

        inner class ViewHolder(v : View) : RecyclerView.ViewHolder(v) {
            val module : TextView = v.findViewById(R.id.dynAssignmentModule)
            val longName : TextView = v.findViewById(R.id.dynAssignmentLongName)
            val shortName : TextView = v.findViewById(R.id.dynAssignmentShortName)
            val score : TextView = v.findViewById(R.id.dynAssignmentScore)
            val menu : ImageView = v.findViewById(R.id.menuAssignment)
        }
    }

    inner class ModuleAdapter(private var modulesList : ModuleCollection) : RecyclerView.Adapter<ModuleAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v : View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_module, parent, false)

            return ViewHolder(v)
        }

        override fun getItemCount(): Int = max(1, modulesList.size)

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (modulesList.isEmpty()) {
                onEmpty(holder)
                return
            }

            // Do not use modulesList[position] as ModuleCollection#get(moduleId)
            // moduleId != position (e.g. some modules could have been deleted)
            modulesList.values.toList()[position]?.let { module ->
                holder.apply {
                    grade.text = Statistics.getModuleGrade(module.semesterNo, module.moduleId).toString()
                    name.text = module.shortName
                    menu.visibility = View.VISIBLE
                    itemView.setOnClickListener {
                        CgpaChart.highlightModule(CgpaChart.generateDenseConfig(requireContext()), dynGraphOverview, requireContext(), module)
                    }
                    menu.setOnClickListener {
                        showPopupMenu(it, module)
                    }
                }
            }

        }

        private fun onEmpty(holder: ViewHolder) {
            holder.apply {
                grade.text = "+"
                name.text = getString(R.string.action_create)
                menu.visibility = View.INVISIBLE
                itemView.setOnClickListener {
                    val intent = Intent(requireContext(), AddModuleActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        internal fun update(newModulesList: ModuleCollection) {
            modulesList = newModulesList
            notifyDataSetChanged()
        }

        private fun showPopupMenu(v: View, module: ModuleEntity) {
            val popup = PopupMenu(v.context, v)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.menu_view, popup.menu)
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_view -> {
                        val intent = Intent(v.context, ViewModuleActivity::class.java)
                        intent.putExtra("semesterNo", module.semesterNo)
                        intent.putExtra("moduleId", module.moduleId)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        inner class ViewHolder(v : View) : RecyclerView.ViewHolder(v) {
            val grade : TextView = v.findViewById(R.id.dynModuleGrade)
            val name : TextView = v.findViewById(R.id.dynModuleShortName)
            val menu : ImageView = v.findViewById(R.id.menuModule)
        }
    }

}