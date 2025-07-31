package sg.com.mapp.gradetracker.util

data class Score(val score: Float, val max: Float, val weight: Float) {
    val percentage: Float
        get() = score / max * 100

    operator fun plus(other: Score) : Score {
        val weightedScore = score/max * weight
        val otherWeightedScore = with(other) { score / max *  weight }

        val newScore = (weightedScore + otherWeightedScore)
        return Score(newScore, weight + other.weight, weight + other.weight)
    }

    fun toGrade() : Grade = Grade.from(percentage)

    companion object {
        /**
         * Calculates the percentage change (i.e. new score is 25% increase of previous score)
         */
        @JvmStatic
        fun change(previous: Score, new: Score) : Float = (new.percentage - previous.percentage)

        @JvmStatic
        val INIT: Score = Score(100f, 100f, 0f)
    }
}