name := "client"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.0"
libraryDependencies += "mysql" % "mysql-connector-java" % "8.0.16"
libraryDependencies += "org.apache.spark"  % "spark-mllib_2.11" % "2.2.0"