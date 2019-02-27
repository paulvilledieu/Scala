import scalaj.http.{Http, HttpOptions}

class Request {
  def sendMessage(m: Message): Unit = {
    Http("http://localhost:9000/").postData(m.message)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
  }
}
