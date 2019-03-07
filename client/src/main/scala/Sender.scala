import Request.sendMessage

object Sender {

  case class Message(m_type: Int, message: String)

  def send_msg(msg: Message) = {
    //post traitements des différents types de messages
    println("Sending " +  msg)
    println(sendMessage(msg.message).right.toString)
  }
}
