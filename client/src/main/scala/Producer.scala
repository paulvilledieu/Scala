object Producer {

  import java.util.Properties
  import org.apache.kafka.clients.producer._

  private
  val server: String = "localhost:9092"

  private
  def send(topic: String, body: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", server)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    val producer = new KafkaProducer[String, String](props)
    val record = new ProducerRecord[String, String](topic, null, body)
    producer.send(record)
    producer.close()
  }

  def sendMessage(body: String): String = {
    send("standard", body)
    ""
  }

  def sendAlert(body: String): String = {
    send("alert", body)
    ""
  }
}
