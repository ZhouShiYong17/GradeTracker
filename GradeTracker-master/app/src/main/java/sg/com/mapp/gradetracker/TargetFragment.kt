package sg.com.mapp.gradetracker

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.component_toolbar.*
import sg.com.mapp.gradetracker.util.Registry
import sg.com.mapp.gradetracker.viewmodel.ToolbarViewModel

class TargetFragment : Fragment() {
    private lateinit var toolbarVM : ToolbarViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_target, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initToolbar()
    }

    private fun initToolbar() {
        toolbar.inflateMenu(R.menu.menu_target)
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
            setGpa(3.99f)
            setGpaType("CUMULATIVE\nGPA")
            setCurrentSemester(context!!, Registry.semesterRepo[1]!!)
            setContextualMessage("Target")
        }
    }

}