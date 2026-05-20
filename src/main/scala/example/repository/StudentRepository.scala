package example.repository

import example.config.DatabaseConfig.db
import example.models.{Student, Subject}
import example.repository.SubjectRepository.subjects
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object StudentRepository {

  val students = TableQuery[StudentTable]

  // CREATE
  def addStudent(student: Student): Future[Int] = {

    db.run(
      students += student
    )
  }

  // BATCH INSERT

  def addStudents(
                   studentList: Seq[Student]
                 ): Future[Option[Int]] = {

    db.run(

      students ++= studentList
    )
  }

  // GET ALL
  def getAllStudents(): Future[Seq[Student]] = {

    db.run(
      students.result
    )
  }
// get by age
  def getStudentsByAge(age: Int): Future[Seq[Student]] = {

    db.run(
      students.filter(_.age === age).result
    )
  }

  // GET BY ID
  def getStudentById(id: Int): Future[Option[Student]] = {

    db.run(
      students.filter(_.id === id).result.headOption
    )
  }

  // UPDATE
  def updateStudent(id: Int, student: Student): Future[Int] = {

    db.run(
      students.filter(_.id === id).update(student)
    )
  }

  // DELETE
  def deleteStudent(id: Int): Future[Int] = {

    db.run(
      students.filter(_.id === id).delete
    )
  }

  // transaction
  def addStudentWithSubject(
                             student: Student,
                             subject: Subject
                           ) = {
    val action = DBIO.seq(
      students += student,
      subjects += subject
    )

    db.run(
      action.transactionally
    )
  }

  // PAGINATION
  def getStudentsPaginated(
                            page: Int,
                            size: Int
                          ): Future[Seq[Student]] = {
    val offset = (page - 1) * size

    db.run(

      students
        .drop(offset)
        .take(size)
        .result
    )
  }
}