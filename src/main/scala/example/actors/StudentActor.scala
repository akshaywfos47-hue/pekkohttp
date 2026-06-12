package example.actors

import example.models.Student
import example.requestdto.CreateStudentWithSubjectRequest
import example.service.StudentService
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import scala.concurrent.duration._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

object StudentActor {

  sealed trait Command

  case class AddStudent(student: Student, replyTo: ActorRef[String]) extends Command
  case object Tick extends Command

  case class GetAllStudents(
                             replyTo: ActorRef[Seq[Student]]
                           ) extends Command

  case class GetStudentsPaginated(
                                   page: Int,
                                   size: Int,
                                   replyTo: ActorRef[Seq[Student]]
                                 ) extends Command

  case class GetStudentsByAge(
                               age: Int,
                               replyTo: ActorRef[Seq[Student]]
                             ) extends Command

  case class AddStudents(
                          studentList: Seq[Student],
                          replyTo: ActorRef[String]
                        ) extends Command

  case class AddStudentWithSubject(
                                    request: CreateStudentWithSubjectRequest,
                                    replyTo: ActorRef[String]
                                  ) extends Command

  case class GetStudentById(
                             id: Int,
                             replyTo: ActorRef[Option[Student]]
                           ) extends Command

  case class UpdateStudent(
                            id: Int,
                            student: Student,
                            replyTo: ActorRef[String]
                          ) extends Command

  case class GetCount(
                       replyTo: ActorRef[Int]
                     ) extends Command

  case class DeleteStudent(
                            id: Int,
                            replyTo: ActorRef[String]
                          ) extends Command

  case class StudentAdded(
                           rows: Int,
                           student: Student,
                           replyTo: ActorRef[String]
                         ) extends Command


  def apply(
             studentService: StudentService,
             totalStudents: Int
           ): Behavior[Command] = {

    Behaviors.withTimers { timers =>

      timers.startTimerAtFixedRate(
        Tick,
        10.seconds
      )

      Behaviors.receive { (context, message) =>

        implicit val ec: ExecutionContextExecutor =
          context.executionContext

        message match {

          case AddStudent(student, replyTo) =>

            context.pipeToSelf(
              studentService.addStudent(student)
            ) {

              case Success(rows) =>
                StudentAdded(
                  rows,
                  student,
                  replyTo
                )

              case Failure(_) =>
                StudentAdded(
                  0,
                  student,
                  replyTo
                )
            }

            Behaviors.same

          case GetAllStudents(replyTo) =>
            studentService
              .getAllStudents()
              .foreach { students =>
                replyTo ! students
              }

            Behaviors.same

          case StudentAdded(
          rows,
          student,
          replyTo
          ) =>

            if (rows > 0) {

              replyTo !
                s"Student ${student.name} added"

              StudentActor(
                studentService,
                totalStudents + 1
              )

            } else {

              replyTo !
                s"Student ${student.name} add failed"

              Behaviors.same
            }


          case GetStudentsPaginated(page, size, replyTo) =>
            studentService
              .getStudentsPaginated(page, size)
              .foreach(replyTo ! _)

            Behaviors.same

          case GetStudentsByAge(age, replyTo) =>
            studentService
              .getStudentsByAge(age)
              .foreach(replyTo ! _)

            Behaviors.same

          case AddStudents(studentList, replyTo) =>
            studentService
              .addStudents(studentList)

            replyTo ! "Students inserted"

            Behaviors.same

          case AddStudentWithSubject(request, replyTo) =>
            studentService
              .addStudentWithSubject(request)

            replyTo !
              "Student and Subject inserted successfully"

            Behaviors.same

          case GetStudentById(id, replyTo) =>
            studentService
              .getStudentById(id)
              .foreach(replyTo ! _)

            Behaviors.same

          case UpdateStudent(id, student, replyTo) =>
            studentService
              .updateStudent(id, student)

            replyTo !
              s"Student $id updated"

            Behaviors.same

          case DeleteStudent(id, replyTo) =>
            studentService
              .deleteStudent(id)

            replyTo !
              s"Student $id deleted"

            Behaviors.same

          case GetCount(replyTo) =>
            replyTo ! totalStudents
            Behaviors.same

          case Tick =>
           // println(s"Current Student Count = $totalStudents")
            Behaviors.same
        }
      }
    }
  }
}