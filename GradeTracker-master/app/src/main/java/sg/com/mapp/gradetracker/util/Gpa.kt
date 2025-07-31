package sg.com.mapp.gradetracker.util

class Gpa {}

/*
class Gpa {
    private var numerator = 0f
    private var denominator = 0f
    val gpa: Float
        get() = numerator / denominator

    private fun calculateCreditsEarned(modules: Map<String, Int>, grades: Map<String, Grade>): Float {
        return grades.map { (k, v) -> v.gradePoint * (modules[k] ?: 0) }.sum()
    }

    fun addSemester(modules: Map<String, Int>, grades: Map<String, Grade>) {
        numerator += calculateCreditsEarned(modules, grades)
        denominator += modules.values.sum().toFloat()
    }

}
*/