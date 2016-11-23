logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Flyway" at "https://flywaydb.org/repo"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")
//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.8")

//addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")

libraryDependencies += "com.h2database" % "h2" % "1.4.192"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.39"

addSbtPlugin("org.scalikejdbc" %% "scalikejdbc-mapper-generator" % "2.4.2")

//addSbtPlugin("org.wartremover" % "wartremover_2.11" % "1.0.1")

addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.0")

//addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
//
