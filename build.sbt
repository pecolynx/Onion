name := "onion"

version := "1.0"

lazy val common = (project in file("modules/common"))

lazy val `onion` = (project in file("."))
  .enablePlugins(PlayScala)
  .aggregate(common)
  .dependsOn(common)

scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint")

//assemblyOutputPath in assembly := file("./akka-http-standalone.jar")

libraryDependencies ++= Seq(jdbc, cache, ws)

//libraryDependencies += "comon" % "common" % "1.0"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "org.scalikejdbc" %% "scalikejdbc" % "2.4.2",
  "org.scalikejdbc" %% "scalikejdbc-config" % "2.4.2",
  "org.springframework" % "spring-web" % "4.2.7.RELEASE",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "com.h2database" % "h2" % "1.4.192",
  "mysql" % "mysql-connector-java" % "5.1.39",
  "joda-time" % "joda-time" % "2.9.4",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "javax.validation" % "validation-api" % "1.0.0.GA",
  "org.glassfish" % "javax.el" % "3.0.0",
  "org.elasticsearch" % "elasticsearch" % "1.7.5",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.5.4",
  "javax.validation" % "validation-api" % "1.0.0.GA",
  "org.hibernate" % "hibernate-core" % "4.3.11.Final",
  "org.hibernate" % "hibernate-validator" % "4.3.2.Final",
  "org.springframework.security" % "spring-security-core" % "4.1.2.RELEASE",
  "org.jsoup" % "jsoup" % "1.9.2",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.specs2" %% "specs2-core" % "3.8.4" % "test",
  "org.flywaydb" %% "flyway-play" % "2.3.0",
  "org.scalikejdbc" %% "scalikejdbc-test" % "2.4.2" % "test",
  "junit" % "junit" % "4.11" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)

//"com.github.tototoshi" %% "play-flyway" % "1.1.3",

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

//Revolver.settings

scalikejdbcSettings

//
//flywayUser := "sa"
//flywayPassword := ""
//flywayUrl := "jdbc:h2:file:./db/hello"

