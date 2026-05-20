package example.service

import example.models.{Student, Subject}
import example.repository.SubjectRepository

import scala.concurrent.Future

class SubjectService(

                      subjectRepository: SubjectRepository.type
                    ) {

  def addSubject(
                  subject: Subject
                ): Future[Int] = {

    subjectRepository
      .addSubject(subject)
  }

  def getAllSubjects():
  Future[Seq[Subject]] = {

    subjectRepository
      .getAllSubjects()
  }

  def getStudentSubjects() = {

    subjectRepository
      .getStudentSubjects()
  }
}