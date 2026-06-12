package example

import example.actors.StudentActor
import example.routes.{StudentRoutes, SubjectRoutes, UserRoutes}
import example.service.ServiceRegistry
import org.apache.pekko.actor.typed.{ActorSystem, Scheduler}
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.server.Directives._
import scala.concurrent.duration._

import scala.concurrent.Await
import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] =
      ActorSystem(Behaviors.empty, "MySystem")

    implicit val scheduler: Scheduler =
      system.scheduler


    val initialCount =
      Await.result(
        ServiceRegistry
          .studentService
          .getStudentCount(),
        5.seconds
      )

// creating a actor and passing a behavior StudentActor with a type student service it is injected by serviceregistry
// see service registry here dependency injection is done
    val studentActor = system.systemActorOf(StudentActor(ServiceRegistry.studentService,  initialCount), "student-actor")



    // COMBINE ALL ROUTES
    val allRoutes =
      concat(
        UserRoutes.route,
        StudentRoutes(studentActor),
        SubjectRoutes.route
      )

    // START SERVER
    Server.start(allRoutes)

    StdIn.readLine()
  }
}