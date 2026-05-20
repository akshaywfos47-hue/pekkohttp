package example.routes

import example.models.{StudentSubjectResponse, Subject}
import example.repository.SubjectRepository
import example.service.ServiceRegistry
import org.apache.pekko.http.scaladsl.server.Directives._
import spray.json._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object SubjectRoutes extends DefaultJsonProtocol {

  implicit val subjectFormat =
    jsonFormat4(Subject)

  implicit val responseFormat = jsonFormat5(StudentSubjectResponse)

  val route =
    pathPrefix("subjects") {

      concat(

        pathEndOrSingleSlash {

          concat(

            // CREATE
            post {

              entity(as[Subject]) { subject =>

                onSuccess(
                  ServiceRegistry.subjectService.addSubject(subject)
                ) { _ =>

                  complete(s"Subject added")
                }
              }
            },

            // GET ALL
            get {

              onSuccess(
                ServiceRegistry.subjectService.getAllSubjects()
              ) { result =>

                complete(result)
              }
            },

          )
        },
        path("join") {

          get {

            onSuccess(
              ServiceRegistry.subjectService.getStudentSubjects()
            ) { result =>

              val response =

                result.map {

                  case (student, subject) =>

                    StudentSubjectResponse(

                      student.id,

                      student.name,

                      subject.id,

                      subject.subjectName,

                      subject.credits
                    )
                }

              complete(response)
            }
          }
        }
      )
    }
}