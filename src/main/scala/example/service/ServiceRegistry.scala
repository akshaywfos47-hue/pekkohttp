package example.service

import example.repository.{StudentRepository, SubjectRepository}

object ServiceRegistry {

  val studentService = new StudentService(StudentRepository)

  val subjectService = new SubjectService(SubjectRepository)
}