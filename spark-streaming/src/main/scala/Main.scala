import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql._
import org.apache.spark.streaming._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object Main extends App {

  case class Test(data: String)

  case class Message(
      objectId: String,
      timestamp: Long,
      latitude: Double,
      longitude: Double,
      temperature: Float,
      batteryRemaining: Int,
      hearRate: Float,
      state: String,
      message: String,
      hdfs_timestamp: Long,
      raw: String)

  val defaultMessage = Message("Error format",
    0, 0, 0, 0, 0, 0, "", "", 0, "")

  implicit val MReader: Reads[Message] = (
    (JsPath \ "objectId").read[String] and
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "latitude").read[Double] and
      (JsPath \ "longitude").read[Double] and
      (JsPath \ "temperature").read[Float] and
      (JsPath \ "batteryRemaining").read[Int] and
      (JsPath \ "hearRate").read[Float] and
      (JsPath \ "state").read[String] and
      (JsPath \ "message").read[String]

  )((f1, f2, f3, f4, f5, f6, f7, f8, f9) =>
  Message(f1, f2, f3, f4, f5, f6, f7, f8, f9, 0, ""))

  implicit val MWrite: Writes[Message] = Json.writes[Message]

  implicit val MFormat: Format[Message] = Format(MReader, MWrite)

  def strToMessage(str: String) = {
    Json.parse(str).validate[Message] match {
      case JsError(_) => defaultMessage.copy(raw=str, hdfs_timestamp = System.currentTimeMillis())
      case JsSuccess(m, _) => m.copy(raw=str, hdfs_timestamp = System.currentTimeMillis())
    }
  }

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
    stream.map(record => strToMessage(record.value())).print()
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
    val topics2 = Array("alert")
    val stream2 = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](topics2, kafkaParams2)
    )
    val dstream = stream2.map(_.value)
    dstream.cache()
    dstream.foreachRDD(
      rdd => {
        val sqlContext = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
        import sqlContext.implicits._
        val df = rdd.toDF("msg")
        df.write.format("parquet").mode("append").save(s"data")
      }
    )
  }

  val sparkConfig = new SparkConf().setMaster("local[*]").setAppName("SparkKafkaStream")
  val streamingContext = new StreamingContext(sparkConfig, Seconds(1))
  streamingContext.sparkContext.setLogLevel("ERROR")

  //saveToDisk(streamingContext)
  //handleAlerts(streamingContext)
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
  stream.map(record => strToMessage(record.value)).print()

  streamingContext.start()
  streamingContext.awaitTermination()
}
