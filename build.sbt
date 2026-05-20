import Dependencies.munit

val pekkoVersion = "1.0.2"
val pekkoHttpVersion = "1.0.1"

lazy val root = (project in file("."))
  .settings(
    name := "pekkohttp",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % pekkoHttpVersion,
      "com.typesafe.slick" %% "slick" % "3.5.1",
      "org.postgresql" % "postgresql" % "42.7.3",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
      munit % Test
    )
  )