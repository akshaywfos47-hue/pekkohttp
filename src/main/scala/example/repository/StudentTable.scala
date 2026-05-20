package example.repository

import example.models.Student
import slick.jdbc.PostgresProfile.api._

class StudentTable(tag: Tag)
  extends Table[Student](tag, "students") {

  def id = column[Int]("id", O.PrimaryKey)

  def name = column[String]("name")

  def age = column[Int]("age")

  def score = column[Double]("score")

  def place = column[String]("place")

  def * =
    (id, name, age, score, place) <> (Student.tupled, Student.unapply)
}