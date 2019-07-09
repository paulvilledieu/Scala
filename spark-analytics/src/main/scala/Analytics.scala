package main.scala

import java.sql.{Connection, DriverManager}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession, SaveMode}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.Bucketizer

import scala.concurrent.ExecutionContext.Implicits.global


object Analytics extends App {

    def readParquets() : DataFrame = {
        val ss = SparkSession.builder
                    .appName("DFWC")
                    .master("local[*]")
                    .getOrCreate()
        ss.read.load("../hdfs-data").dropDuplicates()
    }

    def nbAlerts(df : DataFrame) = {
        df.filter(col("state").isin("Alert"))
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .orderBy(desc("date"))
            .withColumnRenamed("count","nbAlerts")
    } 

    def nbConnectedObjects(df : DataFrame) = {
        df.withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .agg(countDistinct("objectId"))
            .orderBy(desc("date"))
            .withColumnRenamed("count(DISTINCT objectId)","nbConnectedObject")

    }

    def nbPanneObject(df : DataFrame) = {
        df.filter(col("state").isin("Failed"))
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .orderBy(desc("date"))
            .withColumnRenamed("count","nbPanne")
    }

    def nbPanneNorth(df : DataFrame) = {
        df.filter(col("state").isin("Failed") && col("latitude") >= 0)
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .orderBy(desc("date"))
            .withColumnRenamed("count","nbPanneNorth")
    }

    def nbPanneSouth(df : DataFrame) = {
        df.filter(col("state").isin("Failed") && col("latitude") < 0)
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .orderBy(desc("date"))
            .withColumnRenamed("count","nbPanneSouth")
    }

    def nbPanneBattery(df : DataFrame) = {
        df.filter(col("state").isin("Failed"))
            .groupBy("batteryRemaining")
            .count()
            .orderBy(asc("batteryRemaining"))
            .withColumnRenamed("count","nbPanne")
    }

    def nbPanneTemperature(df : DataFrame) = {
        df.filter(col("state").isin("Failed"))
            .withColumn("temperature", round(df.col("temperature")))
            .groupBy("temperature")
            .count()
            .orderBy(asc("temperature"))
            .withColumnRenamed("count","nbPanne")
        
    }


    val df = readParquets()
    val tmp = nbAlerts(df)
                .join(nbConnectedObjects(df), Seq("date"), joinType="outer")
                .join(nbPanneObject(df), Seq("date"), joinType="outer")
                .join(nbPanneNorth(df), Seq("date"), joinType="outer")
                .join(nbPanneSouth(df), Seq("date"), joinType="outer")
    tmp.show()

    val tmp2 = nbPanneBattery(df)
    tmp2.show()

    val tmp3 = nbPanneTemperature(df)
    tmp3.show()

    val prop = new java.util.Properties
    prop.setProperty("user", "root2")
    prop.setProperty("password", "root")
    prop.setProperty("driver", "com.mysql.jdbc.Driver")
    
    val url = "jdbc:mysql://127.0.0.1:3306/scaladb?serverTimezone=UTC"
        
    try {
        tmp.write.jdbc(url, "globals", prop)
    } catch {
        case _: Throwable => tmp.write.mode(SaveMode.Overwrite).jdbc(url, "globals", prop)
    }
    try {
        tmp2.write.jdbc(url, "battery", prop)
    } catch {
        case _: Throwable => tmp2.write.mode(SaveMode.Overwrite).jdbc(url, "battery", prop)
    }
    try {
        tmp3.write.jdbc(url, "temperature", prop)
    } catch {
        case _: Throwable => tmp3.write.mode(SaveMode.Overwrite).jdbc(url, "temperature", prop)
    }
}
