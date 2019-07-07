package main.scala

import java.sql.{Connection, DriverManager}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession, SaveMode}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.Bucketizer
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global


object Analytics extends App {

    def readParquets() : DataFrame = {
        val ss = SparkSession.builder
                    .appName("DFWC")
                    .master("local[*]")
                    .getOrCreate()
        ss.read.load("../hdfs-data")
    }

    def nbAlerts(df : DataFrame) = {
        df.filter(col("state").isin("Alert"))
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .withColumnRenamed("count","nbAlerts")
    } 

    def nbConnectedObjects(df : DataFrame) = {
        df.withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .agg(countDistinct("objectId"))
            .withColumnRenamed("count(DISTINCT objectId)","nbConnectedObject")

    }

    def nbPanneObject(df : DataFrame) = {
        df.filter(col("state").isin("Failed"))
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .withColumnRenamed("count","nbPanne")
    }

    def nbPanneNorth(df : DataFrame) = {
        df.filter(col("state").isin("Failed") && col("latitude") >= 0)
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .withColumnRenamed("count","nbPanneNorth")
    }

    def nbPanneSouth(df : DataFrame) = {
        df.filter(col("state").isin("Failed") && col("latitude") < 0)
            .withColumn("date", (col("timestamp").cast("timestamp").cast("date")))
            .groupBy("date")
            .count()
            .withColumnRenamed("count","nbPanneSouth")
    }

    def nbPanneBattery(df : DataFrame) = {
        df.filter(col("state").isin("Failed"))
            .groupBy("batteryRemaining")
            .count()
            .show
    }

    def nbPanneTemperature(df : DataFrame) = {
        val splits = Range.Double(-15,50,5).toArray
        val bucketizer = new Bucketizer()
                            .setInputCol("temperature")
                            .setOutputCol("bucket")
                            .setSplits(splits)
        val bucketed = bucketizer.transform(df.filter(col("state").isin("Failed")))
        
    }


    val df = readParquets()
    val tmp = nbAlerts(df)
                .join(nbConnectedObjects(df), Seq("date"), joinType="outer")
                .join(nbPanneObject(df), Seq("date"), joinType="outer")
                .join(nbPanneNorth(df), Seq("date"), joinType="outer")
                .join(nbPanneSouth(df), Seq("date"), joinType="outer")
    tmp.show()
    val prop = new java.util.Properties
    prop.setProperty("user", "JhuSBBQqIY")
    prop.setProperty("password", "1Ctsi7w9dR")
    prop.setProperty("driver", "com.mysql.jdbc.Driver")
    
    val url = "jdbc:mysql://remotemysql.com/JhuSBBQqIY"
    
    val table = "globals"
    
    try {
        tmp.write.jdbc(url, table, prop)
    } catch {
        case _: Throwable => tmp.write.mode(SaveMode.Overwrite).jdbc(url, table, prop)
    }
}