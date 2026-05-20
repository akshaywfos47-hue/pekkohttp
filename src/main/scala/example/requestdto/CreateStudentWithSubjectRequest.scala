package example.requestdto

import example.models.Student
import example.models.Subject

case class CreateStudentWithSubjectRequest(

                                            student: Student,

                                            subject: Subject
                                          )