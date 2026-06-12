package example.routes

import example.actors.StudentActor
import example.models.{Student, Subject}
import example.requestdto.CreateStudentWithSubjectRequest
import example.service.ServiceRegistry
import org.apache.pekko.actor.typed.{ActorRef, Scheduler}
import org.apache.pekko.actor.typed.scaladsl.AskPattern.Askable
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout
import spray.json._

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object StudentRoutes extends DefaultJsonProtocol {

  case class MessageResponse(message: String)
  case class CountResponse(count: Int)

  // http response message when  the methods returns int because we should always send string or json as a http response
  implicit val messageResponseFormat: RootJsonFormat[MessageResponse] = jsonFormat1(MessageResponse)

  implicit val studentFormat: RootJsonFormat[Student] = jsonFormat5(Student)
  implicit val subjectFormat: RootJsonFormat[Subject] =jsonFormat4(Subject)
  implicit val requestFormat: RootJsonFormat[CreateStudentWithSubjectRequest] = jsonFormat2(CreateStudentWithSubjectRequest)
  implicit val errorFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse)
  implicit val countResponseFormat = jsonFormat1(CountResponse)

  implicit val timeout: Timeout = 3.seconds


  case class ErrorResponse(
                            message: String
                          )

  def apply(studentActor: ActorRef[StudentActor.Command])
           (implicit scheduler: Scheduler): Route =


    pathPrefix("students") {
      concat(
      pathEndOrSingleSlash {
        concat(

        // CREATE
          post {
            entity(as[Student]) { student =>
              onSuccess(
                studentActor.ask(
                  replyTo =>
                    StudentActor.AddStudent(
                      student,
                      replyTo
                    )
                )
              ) { response =>

                complete(response)
              }
            }
          },




        // GET ALL
          get {
            onSuccess(
              studentActor.ask(
                replyTo =>
                  StudentActor
                    .GetAllStudents(replyTo)
              )
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
              onComplete(
                studentActor.ask(
                  replyTo =>
                    StudentActor.GetStudentsPaginated(
                      page,
                      size,
                      replyTo
                    )
                )
              ) {
                case Success(result) =>
                  complete(result)

                case Failure(ex) =>
                  complete(
                    500,
                    ErrorResponse(ex.getMessage)
                  )
              }
            }
          }
        },

        path("count") {
          get {
            onSuccess(
              studentActor.ask[Int](
                replyTo =>
                  StudentActor.GetCount(replyTo)
              )
            ) { count =>
              complete(
               CountResponse( count)
              )
            }
          }
        },

        //get by query parameter

        path("search"){
          get{
            parameters("age".as[Int]){age=>
              onComplete(
                studentActor.ask(
                  replyTo =>
                    StudentActor.GetStudentsByAge(
                      age,
                      replyTo
                    )
                )
              ) {
                case Success(result) =>
                  complete(result)

                case Failure(ex) =>
                  complete(
                    500,
                    ErrorResponse(ex.getMessage)
                  )
              }
            }
          }
        },

        // batch insert
        path("batch") {
          post {
            entity(as[Seq[Student]]) { studentList =>

              onComplete(
                studentActor.ask(
                  replyTo =>
                    StudentActor.AddStudents(
                      studentList,
                      replyTo
                    )
                )
              ) {

                case Success(_) =>
                  complete(MessageResponse("inserted many students "))

                case Failure(ex) =>
                  complete(
                    500,
                    ErrorResponse(ex.getMessage)
                  )
              }
            }
          }
        },

        // TRANSACTION
        path("transaction") {
          post {
            entity(as[CreateStudentWithSubjectRequest]) { request =>
              onComplete(
                studentActor.ask(
                  replyTo =>
                    StudentActor.AddStudentWithSubject(
                      request,
                      replyTo
                    )
                )
              ) {

                case Success(result) =>
                  complete(MessageResponse(s"transaction successful __ $result"))

                case Failure(ex) =>
                  complete(
                    500,
                    ErrorResponse(ex.getMessage)
                  )
              }
            }
          }
        },




      // GET BY ID
      path(IntNumber) { id =>
        concat(
        get {
          onComplete(
            studentActor.ask(
              replyTo =>
                StudentActor.GetStudentById(
                  id,
                  replyTo
                )
            )
          )  {

            case Success(result) =>
              complete(result)

            case Failure(ex) =>
              complete(
                500,
                ErrorResponse(ex.getMessage)
              )
          }
        } ,


        // UPDATE
        put {
          entity(as[Student]) { student =>
            onComplete(
              studentActor.ask(
                replyTo =>
                  StudentActor.UpdateStudent(
                    id,
                    student,
                    replyTo
                  )
              )
            ){

              case Success(result) =>
                complete(MessageResponse(s"update successfull roe affected - $result"))

              case Failure(ex) =>
                complete(
                  500,
                  ErrorResponse(ex.getMessage)
                )
            }
          }
        } ,

        // DELETE
        delete {
          onComplete(
            studentActor.ask(
              replyTo =>
                StudentActor.DeleteStudent(
                  id,
                  replyTo
                )
            )
          ) {

            case Success(result) =>
              complete(MessageResponse(s"deleted successfull row affected - $result"))

            case Failure(ex) =>
              complete(
                500,
                ErrorResponse(ex.getMessage)
              )
          }
        }
        )
      }
      )

    }
}