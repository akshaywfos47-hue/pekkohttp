package example.models

case class StudentSubjectResponse(

                                   studentId: Int,

                                   studentName: String,

                                   subjectId: Int,

                                   subjectName: String,

                                   credits: Option[Int]
                                 )