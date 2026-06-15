package example.routes

import example.models.{StudentSubjectResponse, Subject}
import example.repository.SubjectRepository
import example.actors.SubjectPersistentActor
import example.service.ServiceRegistry
import org.apache.pekko.http.scaladsl.server.Directives._
import spray.json._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.Scheduler
import org.apache.pekko.actor.typed.scaladsl.AskPattern.Askable
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout

import scala.concurrent.duration.DurationInt
object SubjectRoutes extends DefaultJsonProtocol {

  implicit val timeout: Timeout =
    3.seconds

  implicit val subjectFormat =
    jsonFormat4(Subject)

  implicit val responseFormat = jsonFormat5(StudentSubjectResponse)

  def apply(
             subjectActor:
             ActorRef[
               SubjectPersistentActor.Command
             ]
           )(
             implicit scheduler: Scheduler
           ): Route  =
    pathPrefix("subjects") {

      concat(

        pathEndOrSingleSlash {

          concat(

            // CREATE
            post {

              entity(as[Subject]) { subject =>

                onSuccess(

                  subjectActor.ask(

                    replyTo =>

                      SubjectPersistentActor
                        .AddSubject(
                          subject,
                          replyTo
                        )

                  )

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