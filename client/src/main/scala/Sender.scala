import Request.{sendMessage, sendAlert}
import play.api.libs.json._

object Sender {

  case class Message(id: String,
                     timestamp: Long,
                     battery: Int,
                     longitude: Double,
                     latitude: Double,
                     state: String,
                     msg: String,
                     hearth_rate: Float,
                     temperature: Float,
                     msg_type: Int)

  def csv_parser(str: String): Message  =  {
    val msg = str.split(',')
    create_message(msg)
  }

  def json_parser(str: String): Message = {
    implicit val messageJsonFormat: OFormat[Message] = Json.format[Message]
    Json.parse(str).as[Message]
  }

  private
  def create_message(params: Array[String]) = {
    Message(
      params.head,
      params(1).toLong,
      params(2).toInt,
      params(3).toDouble,
      params(4).toDouble,
      params(5),
      params(6),
      params(7).toFloat,
      params(8).toFloat,
      params(9).toInt)
  }

  private
  implicit val messageWrites: Writes[Message] = (message: Message) => Json.obj(
    "objectId" -> message.id,
    "timestamp" -> message.timestamp,
    "latitude" -> message.latitude,
    "longitude" -> message.longitude,
    "temperature" -> message.temperature,
    "batteryRemaining" -> message.battery,
    "heartRate" -> message.hearth_rate,
    "state" -> message.state,
    "message" -> message.msg
  )

  def send_msg(msg: Message) = {
    val json = Json.toJson(msg)(messageWrites)
    println("Sending " +  json.toString())
    if (msg.msg_type > 0)
      println(sendAlert(json.toString()).right.toString)
    else
      println(sendMessage(json.toString()).right.toString)
  }
}
