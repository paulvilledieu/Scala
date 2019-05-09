import Request.sendMessage
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
                     something: Int,
                     temperature: Float)

  def csv_parser(str: String): Message  =  {
    val msg = str.split(',')
    create_message(msg)
  }


  def json_parser(str: String): Message = {

  }

  def create_message(params: Array[String]) = {
    Message(
      params.head,
      params(1).toLong,
      params(2).toInt,
      params(3).toDouble,
      params(4).toDouble,,
      params(5),
      params(6),
      params(7).toFloat,
      params(8).toInt,
      params(9).toFloat)
  }


  def send_msg(msg: Message) = {
    //post traitements des différents types de messages
    println("Sending " +  msg)
    println(sendMessage(msg.message).right.toString)
  }
}
