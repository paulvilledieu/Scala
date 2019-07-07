import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

import Mail.Mail

object Message {

  case class Message(
                      objectId: String,
                      timestamp: Long,
                      latitude: Double,
                      longitude: Double,
                      temperature: Float,
                      batteryRemaining: Int,
                      heartRate: Float,
                      state: String,
                      message: String,
                      hdfs_timestamp: Long = 0,
                      raw: String = "")

  val defaultMessage = Message("Error format",
    0, 0, 0, 0, 0, 0, "", "")

  implicit val MReader: Reads[Message] = (
    (JsPath \ "objectId").read[String] and
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "latitude").read[Double] and
      (JsPath \ "longitude").read[Double] and
      (JsPath \ "temperature").read[Float] and
      (JsPath \ "batteryRemaining").read[Int] and
      (JsPath \ "heartRate").read[Float] and
      (JsPath \ "state").read[String] and
      (JsPath \ "message").read[String]
    )((f1, f2, f3, f4, f5, f6, f7, f8, f9) =>
    Message(f1, f2, f3, f4, f5, f6, f7, f8, f9))

  implicit val MWrite: Writes[Message] = Json.writes[Message]

  implicit val MFormat: Format[Message] = Format(MReader, MWrite)

  def strToMessage(str: String) = {
    Json.parse(str).validate[Message] match {
      case JsError(_) => defaultMessage.copy(raw=str, hdfs_timestamp = System.currentTimeMillis())
      case JsSuccess(m, _) => m.copy(raw=str, hdfs_timestamp = System.currentTimeMillis())
    }
  }

  def messageToMail(message: Message): Mail = {
    new Mail(
      from=("scalasparkepita@gmail.com", "Spark EPITA"),
      to=Seq("scalasparkepita@gmail.com"),
      subject=message.state,
      message=message.message
    )
  }
}
