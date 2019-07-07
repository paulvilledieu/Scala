import org.apache.spark.SparkConf
import org.apache.spark.sql._
import org.apache.spark.streaming._


import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

import Message.{strToMessage, messageToMail}
import Mail.sendMail

object Main extends App {

  def saveToDisk(streamingContext: StreamingContext): Unit = {
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "0",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("standard", "alert")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    )
    stream.map(record => strToMessage(record.value))
      .cache()
      .foreachRDD(rdd => {
        if (rdd.count() > 0) {
          val sqlContext = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
          import sqlContext.implicits._
          val df = rdd.toDF()
          df.write.format("parquet").mode("append").save(s"../hdfs-data")
        }
    })
  }

  def handleAlerts(streamingContext: StreamingContext): Unit = {
    val kafkaParams2 = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "1",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("alert")
    val stream = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams2)
    )
    stream.map(x => strToMessage(x.value))
        .cache()
        .foreachRDD(rdd => {
          rdd.filter(_.state.contains("Alert"))
            .map(messageToMail)
            .foreach(sendMail)
        })
  }

  val sparkConfig = new SparkConf().setMaster("local[*]").setAppName("SparkKafkaStream")
  val streamingContext = new StreamingContext(sparkConfig, Seconds(1))
  streamingContext.sparkContext.setLogLevel("ERROR")

  saveToDisk(streamingContext)
  handleAlerts(streamingContext)

  streamingContext.start()
  streamingContext.awaitTermination()
}
