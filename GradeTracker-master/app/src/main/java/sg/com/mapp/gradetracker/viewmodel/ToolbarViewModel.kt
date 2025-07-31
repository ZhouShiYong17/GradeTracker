package sg.com.mapp.gradetracker.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import sg.com.mapp.gradetracker.R
import sg.com.mapp.gradetracker.data.SemesterEntity

class ToolbarViewModel : ViewModel() {
    private val _currentGpa = MutableLiveData<String>()
    private val _gpaType = MutableLiveData<String>()
    private val _currentSemester = MutableLiveData<String>()
    private val _contextualMessage = MutableLiveData<String>()

    val currentGpa : LiveData<String> = _currentGpa
    val gpaType : LiveData<String> = _gpaType
    val currentSemester : LiveData<String> = _currentSemester
    val contextualMessage : LiveData<String> = _contextualMessage

    fun setGpa(gpa: Float) {
        _currentGpa.value = "%.2f".format(gpa)
    }

    fun setGpaType(gpaType: String) {
        _gpaType.value = gpaType
    }

    fun setCurrentSemester(context: Context, semesterEntity: SemesterEntity) {
        val year = semesterEntity.year
        val semester = semesterEntity.semester

        _currentSemester.value = context.applicationContext.getString(R.string.format_semester, year, semester)
    }

    fun setContextualMessage(string: String) {
        _contextualMessage.value = string
    }
}