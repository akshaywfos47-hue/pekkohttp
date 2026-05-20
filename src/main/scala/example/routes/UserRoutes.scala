package example.routes

import example.models.User
import org.apache.pekko.http.scaladsl.server.Directives._
import spray.json._
import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object UserRoutes extends DefaultJsonProtocol {

  implicit val userFormat = jsonFormat4(User)

  var users = scala.collection.mutable.Map[Int, User]()

  val route =
    pathPrefix("users") {

      concat(

        // CREATE + GET ALL
        pathEndOrSingleSlash {

          concat(

            // CREATE USER
            post {
              entity(as[User]) { user =>
                users += (user.id -> user)
                complete(s"User ${user.name} added")
              }
            },

            // GET ALL USERS
            get {
              complete(users.values.toList)
            }
          )
        },

        // GET BY ID + UPDATE + DELETE
        path(IntNumber) { id =>

          concat(

            // GET USER BY ID
            get {
              complete(users.get(id))
            },

            // UPDATE USER
            put {
              entity(as[User]) { user =>
                users.update(id, user)
                complete(s"User $id updated")
              }
            },

            // DELETE USER
            delete {
              users.remove(id)
              complete(s"User $id deleted")
            }
          )
        }
      )
    }
}