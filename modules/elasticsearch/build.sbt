name := "elasticsearch"

version := "1.0"
//
//lazy val common = (project in file("../modules/common"))
//lazy val http = (project in file("../modules/http"))
//
////lazy val elasticsearch = (project in file("modules/elasticsearch"))
//lazy val elasticsearch = (project in file("."))
//  .aggregate(common)
//  .dependsOn(common)

scalaVersion := "2.11.7"

// Product

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.5"

libraryDependencies += "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.5.0"

libraryDependencies += "joda-time" % "joda-time" % "2.9.4"

libraryDependencies += "org.elasticsearch" % "elasticsearch" % "1.7.5"

libraryDependencies += "org.scalikejdbc" %% "scalikejdbc" % "2.4.2"

libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-config" % "2.4.2"

libraryDependencies += "javax.validation" % "validation-api" % "1.0.0.GA"

libraryDependencies += "org.hibernate" % "hibernate-core" % "4.3.11.Final"

libraryDependencies += "org.hibernate" % "hibernate-validator" % "4.3.2.Final"

libraryDependencies += "javax.validation" % "validation-api" % "1.0.0.GA"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.39"

// Test

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.1" % "test"

libraryDependencies += "org.scalikejdbc" %% "scalikejdbc-test" % "2.4.2" % "test"


scalikejdbcSettings

