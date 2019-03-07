import scalaj.http.{Http, HttpOptions}

object Request {

  def sendMessage(body: String) = {
    try {
      Right(Http("http://localhost:9000/").postData(body)
        .header("Content-Type", "application/json")
        .header("Charset", "UTF-8")
        .option(HttpOptions.readTimeout(10000))
        .asString)
    } catch {
      //case java.net.ConnectException => Left("Connection Exception")
      case exception: Exception => Left(exception.toString)
    }
  }
}
