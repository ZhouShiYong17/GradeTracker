package sg.com.mapp.gradetracker.data.source.local

import android.arch.persistence.room.TypeConverter
import sg.com.mapp.gradetracker.util.Grade
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun gradeToString(grade: Grade?): String? = grade?.name

    @TypeConverter
    fun stringToGrade(string: String?): Grade? = string?.let { Grade.valueOf(it) }
}
