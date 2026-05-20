package example.repository

import example.models.Subject
import example.repository.StudentRepository.students
import slick.jdbc.PostgresProfile.api._

class SubjectTable(tag: Tag)
  extends Table[Subject](tag, "subjects") {

  def id =
    column[Int]("id", O.PrimaryKey)

  def studentId =
    column[Int]("student_id")

  def subjectName =
    column[String]("subject_name")

  def credits =
    column[Option[Int]]("credits")

  // FOREIGN KEY

  def studentFk =

    foreignKey(
      "fk_student",
      studentId,
      students
    )(_.id)

  def * =
    (id, studentId, subjectName,credits) <> (Subject.tupled, Subject.unapply)
}