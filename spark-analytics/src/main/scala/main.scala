package main.scala

import java.sql.{Connection, DriverManager}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object ScalaJdbcConnectSelect extends App {

    def sendDataToDb() {
        val url = "jdbc:mysql://bdibumfmo64iyew7hw6o-mysql.services.clever-cloud.com/bdibumfmo64iyew7hw6o"
        val driver = "com.mysql.jdbc.Driver"
        val username = "uqs1ur4rbnekm742"
        val password = "NAHfbrUmNL3HXSPK7yzZ"
        try {
            Class.forName(driver)
            val connection:Connection = DriverManager.getConnection(url, username, password)
            val statement = connection.createStatement
            //statement.executeUpdate("INSERT INTO data " + "VALUES (2,2,2,2,2,2,2,2,'ds', 'ds', 0) ")
            connection.close
        } catch {
            case e: Exception => e.printStackTrace
        }
    }

    def readParquets() {
        val ss = SparkSession.builder
                    .appName("DFWC")
                    .master("local[*]")
                    .getOrCreate()
        import ss.implicits._
        val df = ss.read.load("../hdfs-data")
        df.collect.foreach(println)

    }

    readParquets()
}