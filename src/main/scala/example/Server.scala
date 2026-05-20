package example

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route

import scala.concurrent.ExecutionContextExecutor

object Server {

  def start(route: Route)(implicit system: ActorSystem[_]): Unit = {

    implicit val ec: ExecutionContextExecutor = system.executionContext

    val config = system.settings.config
    val host = config.getString("server.host")
    val port = config.getInt("server.port")

    Http().newServerAt(host, port).bind(route)

    println(s"Server started at http://$host:$port/")
  }
}