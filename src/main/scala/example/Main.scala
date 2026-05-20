package example

import example.routes.{StudentRoutes, SubjectRoutes, UserRoutes}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.server.Directives._

import scala.io.StdIn

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] =
      ActorSystem(Behaviors.empty, "MySystem")

    // COMBINE ALL ROUTES
    val allRoutes =
      concat(
        UserRoutes.route,
        StudentRoutes.route,
        SubjectRoutes.route
      )

    // START SERVER
    Server.start(allRoutes)

    StdIn.readLine()
  }
}