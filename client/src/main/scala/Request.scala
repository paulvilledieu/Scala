import scalaj.http.{Http, HttpOptions}

object Request {

  def sendMessage(body: String): String = {
    Http("http://localhost:9000/").postData(body)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString.body
  }
}
