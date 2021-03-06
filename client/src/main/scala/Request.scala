import scalaj.http.{Http, HttpOptions, HttpResponse}

object Request {

  private
  val uri: String = "http://localhost:9000/"

  private
  def send(uri: String, body: String) = {
    try {
      Right(Http(uri).postData(body)
        .header("Content-Type", "application/json")
        .header("Charset", "UTF-8")
        .option(HttpOptions.readTimeout(10000))
        .asString.statusLine)
    } catch {
      //case java.net.ConnectException => Left("Connection Exception")
      case exception: Exception => Left(exception.getMessage)
    }
  }

  def sendMessage(body: String): Either[String, String] = {
    send(uri + "datas", body)
  }

  def sendAlert(body: String): Either[String, String] = {
    send(uri + "alerts", body)
  }


}
