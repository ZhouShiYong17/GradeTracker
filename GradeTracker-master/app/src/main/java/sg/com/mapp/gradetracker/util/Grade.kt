package sg.com.mapp.gradetracker.util

enum class Grade(private val _name: String, val gradePoint: Float, val minScore: Float, val description: String) {
    F      ("F" ,0.0f,0f,"Fail"),
    D_MINUS("D-",0.5f,1f,"Subsidiary Pass"),
    D      ("D" ,1.0f,50f, "Pass"),
    D_PLUS ("D+", 1.5f, 55f, "Good Pass"),
    C      ("C" , 2.0f, 60f, "Credit"),
    C_PLUS ("C+", 2.5f, 65f, "Good Credit"),
    B      ("B" , 3.0f, 70f, "Good"),
    B_PLUS ("B+", 3.5f, 75f, "Very Good"),
    A      ("A" , 4.0f, 80f, "Excellent");

    override fun toString() : String = _name

    companion object {
        fun from(score: Float) : Grade {
            for (grade in values().sortedByDescending { it.minScore }) {
                if (score >= grade.minScore) return grade
            }

            return F
        }
    }

}
