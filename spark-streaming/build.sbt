name := "Test spark-streaming"
version := "1.0"
scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % "2.2.0",
        "org.apache.spark" %% "spark-sql" % "2.2.0",
        "org.apache.spark" %% "spark-streaming" % "2.2.0",
        "org.apache.spark" %% "spark-sql-kafka-0-10" % "2.2.0"
        )
libraryDependencies += "org.apache.spark" % "spark-streaming-kafka-0-10_2.11" % "2.0.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.4.0"

