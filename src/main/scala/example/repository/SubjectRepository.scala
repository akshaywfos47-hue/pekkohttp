package example.repository

import example.config.DatabaseConfig.db
import example.models.Subject
import example.repository.StudentRepository.students
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object SubjectRepository {

  val subjects =
    TableQuery[SubjectTable]

  // CREATE
  def addSubject(subject: Subject): Future[Int] = {

    db.run(
      subjects += subject
    )
  }

  // GET ALL
  def getAllSubjects(): Future[Seq[Subject]] = {

    db.run(
      subjects.result
    )
  }

  //join
  def getStudentSubjects() = {

    db.run(

      students
        .join(subjects)
        .on(_.id === _.studentId)
        .result
    )
  }
}