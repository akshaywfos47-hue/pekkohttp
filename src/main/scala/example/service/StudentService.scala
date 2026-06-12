package example.service

import example.models.{Student, Subject}
import example.repository.StudentRepository
import example.requestdto.CreateStudentWithSubjectRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class StudentService(

                      studentRepository: StudentRepository.type
                    ) {

  def addStudent(
                  student: Student
                ): Future[Int] = {

    studentRepository.addStudent(student)
  }

  def getAllStudents():
  Future[Seq[Student]] = {

    studentRepository.getAllStudents()
  }

  def getStudentsPaginated(

                            page: Int,

                            size: Int

                          ): Future[Seq[Student]] = {

    studentRepository
      .getStudentsPaginated(page, size)
  }

  def getStudentsByAge(
                        age: Int
                      ): Future[Seq[Student]] = {

    studentRepository
      .getStudentsByAge(age)
  }

  def addStudents(
                   studentList: Seq[Student]
                 ): Future[Option[Int]] = {

    studentRepository
      .addStudents(studentList)
  }

  def addStudentWithSubject(

                             request: CreateStudentWithSubjectRequest

                           ) = {

    studentRepository
      .addStudentWithSubject(

        request.student,

        request.subject
      )
  }

  def getStudentById(
                      id: Int
                    ): Future[Option[Student]] = {

    studentRepository
      .getStudentById(id)
  }

  def updateStudent(

                     id: Int,

                     student: Student

                   ): Future[Int] = {

    studentRepository
      .updateStudent(id, student)
  }

  def deleteStudent(
                     id: Int
                   ): Future[Int] = {

    studentRepository
      .deleteStudent(id)
  }

  def getStudentCount() =
    studentRepository.getAllStudents()
      .map(_.size)
}