package sg.com.mapp.gradetracker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_add_module.*
import kotlinx.android.synthetic.main.content_add_module.*

class AddModuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_module)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(ContextCompat.getDrawable(applicationContext, R.drawable.ic_action_close))
        imageView7.setOnClickListener { view ->
            Snackbar.make(view, "Provide an abbreviation of the module name to use.", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Unsaved Changes")
            .setMessage("The module has not been saved yet. You will lose any changes if you exit.")
            .setPositiveButton(android.R.string.ok) { _, _ -> super.onBackPressed() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> Unit }

        // 3. Get the AlertDialog from create()
        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
