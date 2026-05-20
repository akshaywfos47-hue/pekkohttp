package example.models

case class Subject(
                    id: Int,
                    studentId: Int,
                    subjectName: String,
                    credits: Option[Int]
                  )