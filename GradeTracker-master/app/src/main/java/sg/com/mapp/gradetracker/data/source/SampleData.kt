package sg.com.mapp.gradetracker.data.source

import sg.com.mapp.gradetracker.data.AssignmentEntity
import sg.com.mapp.gradetracker.data.ModuleEntity
import sg.com.mapp.gradetracker.data.SemesterEntity
import sg.com.mapp.gradetracker.util.Grade
import java.util.*

object SampleData {
    val sampleSemesters = listOf(
        SemesterEntity(1, 3.1f),
        SemesterEntity(2, 3.2f),
        SemesterEntity(3, 3.3f),
        SemesterEntity(4, 3.4f),
        SemesterEntity(5, 3.5f),
        SemesterEntity(6, 3.6f)
    )

    val sampleModules = listOf(
        ModuleEntity(1, 1, "STxxxx", "DSP", "Digital Signal Processing", 4, Grade.B_PLUS),
        ModuleEntity(2, 1, "STxxxx", "LD", "Logic Design", 4, Grade.C),
        ModuleEntity(3, 1, "STxxxx", "DCN", "Data Communication & Networking", 4, Grade.C_PLUS),
        ModuleEntity(4, 1, "STxxxx", "MS", "Microcontroller Systems", 4, Grade.B),
        ModuleEntity(5, 1, "STxxxx", "EM IIB", "Engg Maths IIB", 4, Grade.A),

        ModuleEntity(1,2, "STxxxx", "DC", "Digital Circuits", 5, Grade.D),
        ModuleEntity(2, 2, "STxxxx", "COMH", "Computer Hardware", 4, Grade.C),
        ModuleEntity(3, 2, "STxxxx", "IDEA", "Innovation, Design & Enterprise in Action", 2, Grade.C_PLUS),
        ModuleEntity(4, 2, "STxxxx", "EC", "Electronic Circuits", 4, Grade.B),
        ModuleEntity(5, 2, "STxxxx", "EM", "Engg Maths", 4, Grade.F),

        ModuleEntity(1, 3, "STxxxx", "N&P", "Network & Protocols", 4, Grade.B_PLUS),
        ModuleEntity(2, 3, "STxxxx", "LD", "Logic Design", 3, Grade.C),
        ModuleEntity(3, 3, "STxxxx", "CELEC", "Communication Electronics", 4, Grade.C_PLUS),
        ModuleEntity(4, 3, "STxxxx", "EM2", "Engg Maths II", 4, Grade.B),
        ModuleEntity(5, 3, "STxxxx", "COMP", "Computer Programming", 3, Grade.A)
    )

    val sampleAssignments = listOf(
        AssignmentEntity(1, 1, 1, "Quiz", "Quiz", 30f, 100f, null, 70f, Date()),
        AssignmentEntity(2, 1, 3, "GP", "General Performance", 10f, 100f, null, 96f, Date()),
        AssignmentEntity(3, 1, 3, "Quiz1", "Quiz 1", 20f, 100f, null, 62f, Date()),
        AssignmentEntity(4, 1, 2, "CA1", "CA1", 30f, 100f, null, 48f, Date()),
        AssignmentEntity(5, 1, 3, "CA1", "CA1", 20f, 100f, null, 63f, Date()),
        AssignmentEntity(6, 1, 4, "CP", "Class Participation", 15f, 100f, null, 91f, Date()),
        AssignmentEntity(7, 1, 5, "MST", "MST", 20f, 100f, null, 83f, Date()),
        AssignmentEntity(8, 1, 4, "P1", "Practical 1", 15f, 100f, null, 89f, Date()),
        AssignmentEntity(9, 1, 5, "GP", "General Performance", 10f, 100f, null, 98f, Date()),
        AssignmentEntity(10, 1, 5, "P1", "Practical 1", 10f, 100f, null, 89f, Date()),
        AssignmentEntity(11, 1, 1, "MST", "MST", 20f, 100f, null, 68f, Date()),
        AssignmentEntity(12, 1, 4, "MST", "MST", 30f, 100f, null, 68f, Date()),
        AssignmentEntity(13, 1, 2, "CA2", "CA2", 30f, 100f, null, 60f, Date()),
        AssignmentEntity(14, 1, 5, "P2", "P2", 10f, 100f, null, 85f, Date()),
        AssignmentEntity(15, 1, 3, "CA2", "CA2", 20f, 100f, null, 63f, Date()),
        AssignmentEntity(16, 1, 2, "CA3", "CA3", 40f, 100f, null, 78f, Date()),
        AssignmentEntity(17, 1, 5, "Exam", "Exam", 50f, 100f, null, 73f, Date()),
        AssignmentEntity(18, 1, 3, "Exam", "Exam", 30f, 100f, null, 70f, Date()),
        AssignmentEntity(19, 1, 4, "Exam", "Exam", 40f, 100f, null, 67f, Date()),
        AssignmentEntity(20, 1, 1, "Exam", "Exam", 50f, 100f, null, 82f, Date()),

        AssignmentEntity(1, 2, 1, "SMPL", "Sample D", 100f, 100f, null, 50f, Date()),
        AssignmentEntity(2, 2, 2, "SMPL", "Sample C", 100f, 100f, null, 60f, Date()),
        AssignmentEntity(3, 2, 3, "SMPL", "Sample C+", 100f, 100f, null, 65f, Date()),
        AssignmentEntity(4, 2, 4, "SMPL", "Sample B", 100f, 100f, null, 70f, Date()),
        AssignmentEntity(5, 2, 5, "SMPL", "Sample F", 100f, 100f, null, 0f, Date()),

        AssignmentEntity(1, 3, 1, "SMPL", "Sample B+", 100f, 100f, null, 75f, Date()),
        AssignmentEntity(2, 3, 2, "SMPL", "Sample C", 100f, 100f, null, 60f, Date()),
        AssignmentEntity(3, 3, 3, "SMPL", "Sample C+", 100f, 100f, null, 65f, Date()),
        AssignmentEntity(4, 3, 4, "SMPL", "Sample B", 100f, 100f, null, 70f, Date()),
        AssignmentEntity(5, 3, 5, "SMPL", "Sample A", 100f, 100f, null, 80f, Date())
    )

    val __sampleAssignments = listOf(
        AssignmentEntity(1, 1, 1, "Quiz", "Quiz", 30f, 100f, null, 70f, Date()),
        AssignmentEntity(1, 1, 1, "MST", "MST", 20f, 100f, null, 68f, Date()),
        AssignmentEntity(1, 1, 1, "Exam", "Exam", 50f, 100f, null, 82f, Date()),

        AssignmentEntity(1, 1, 2, "CA1", "CA1", 30f, 100f, null, 48f, Date()),
        AssignmentEntity(1, 1, 2, "CA2", "CA2", 30f, 100f, null, 60f, Date()),
        AssignmentEntity(1, 1, 2, "CA3", "CA3", 40f, 100f, null, 78f, Date()),

        AssignmentEntity(1, 1, 3, "GP", "General Participation", 10f, 100f, null, 96f, Date()),
        AssignmentEntity(1, 1, 3, "Quiz1", "Quiz 1", 20f, 100f, null, 62f, Date()),
        AssignmentEntity(1, 1, 3, "CA1", "CA1", 20f, 100f, null, 63f, Date()),
        AssignmentEntity(1, 1, 3, "CA2", "CA2", 20f, 100f, null, 63f, Date()),
        AssignmentEntity(1, 1, 3, "Exam", "Exam", 30f, 100f, null, 70f, Date()),

        AssignmentEntity(1, 1, 4, "P1", "Practical 1", 15f, 100f, null, 89f, Date()),
        AssignmentEntity(1, 1, 4, "CP", "CP", 15f, 100f, null, 91f, Date()),
        AssignmentEntity(1, 1, 4, "MST", "MST", 30f, 100f, null, 68f, Date()),
        AssignmentEntity(1, 1, 4, "Exam", "Exam", 40f, 100f, null, 67f, Date()),

        AssignmentEntity(1, 1, 5, "MST", "MST", 20f, 100f, null, 83f, Date()),
        AssignmentEntity(1, 1, 5, "GP", "General Participation", 10f, 98f, null, 62f, Date()),
        AssignmentEntity(1, 1, 5, "P1", "P1", 10f, 100f, null, 89f, Date()),
        AssignmentEntity(1, 1, 5, "P2", "P2", 10f, 100f, null, 85f, Date()),
        AssignmentEntity(1, 1, 5, "Exam", "Exam", 50f, 100f, null, 73f, Date())


    )
}