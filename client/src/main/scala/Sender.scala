import Request.sendMessage

object Sender {

  case class Message(m_type: Int, message: String)

  def send_msg(msg: Message): String = {
    //post traitements des différents types de messages
    println("Sending Message")
    sendMessage(msg.message)
    "Good"
  }

}
