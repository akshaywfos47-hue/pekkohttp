package example.routes

import example.models.{Student, StudentSubjectResponse, Subject}
import example.repository.StudentRepository
import example.requestdto.CreateStudentWithSubjectRequest
import org.apache.pekko.http.scaladsl.server.Directives._
import spray.json._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import example.service.ServiceRegistry

object StudentRoutes extends DefaultJsonProtocol {

  implicit val studentFormat = jsonFormat5(Student)
  implicit val subjectFormat=jsonFormat4(Subject)
  implicit val requestFormat = jsonFormat2(CreateStudentWithSubjectRequest)



  val route =
    pathPrefix("students") {
      concat(
      pathEndOrSingleSlash {
        concat(

        // CREATE
        post {
          entity(as[Student]) { student =>
            onSuccess(
              ServiceRegistry
                .studentService
                .addStudent(student)
            ) { _ =>

              complete(s"Student ${student.name} added")
            }
          }
        },


        // GET ALL
        get {
          onSuccess(
            ServiceRegistry
              .studentService
              .getAllStudents()
          ) { result =>

            complete(result)
          }
        }
        )

      },

        // PAGINATION
        path("pagination") {

          get {

            parameters(

              "page".as[Int],

              "size".as[Int]

            ) { (page, size) =>

              onSuccess(
                ServiceRegistry.studentService.getStudentsPaginated(
                  page,
                  size
                )
              ) { result =>
                complete(result)
              }
            }
          }
        },


        //get by query parameter

        path("search"){
          get{
            parameters("age".as[Int]){age=>
              onSuccess(
                ServiceRegistry.studentService.getStudentsByAge(age)
              ) { result =>

                complete(result)
              }

            }
          }

        },


        // batch insert
        path("batch") {

          post {

            entity(as[Seq[Student]]) { studentList =>

              onSuccess(

                ServiceRegistry.studentService.addStudents(studentList)

              ) { result =>

                complete(s"Inserted ${result.getOrElse(0)} students")
              }
            }
          }
        },

        // TRANSACTION
        path("transaction") {
          post {
            entity(as[CreateStudentWithSubjectRequest]) { request =>
              onSuccess(
                ServiceRegistry.studentService.addStudentWithSubject(
                  request
                )
              ) {
                complete("Student and Subject inserted successfully")
              }
            }
          }
        },




      // GET BY ID
      path(IntNumber) { id =>
        concat(
        get {
          onSuccess(
            ServiceRegistry.studentService.getStudentById(id)
          ) { result =>

            complete(result)
          }
        } ,


        // UPDATE
        put {
          entity(as[Student]) { student =>
            onSuccess(
              ServiceRegistry.studentService.updateStudent(id, student)
            ) { _ =>

              complete(s"Student $id updated")
            }
          }
        } ,

        // DELETE
        delete {
          onSuccess(
            ServiceRegistry.studentService.deleteStudent(id)
          ) { _ =>

            complete(s"Student $id deleted")
          }
        }
        )
      }
      )

    }
}