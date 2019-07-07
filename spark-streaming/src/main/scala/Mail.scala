import org.apache.commons.mail._

object Mail {

  case class Mail(
     from: (String, String),
     to: Seq[String],
     cc: Seq[String] = Seq.empty,
     bcc: Seq[String] = Seq.empty,
     subject: String,
     message: String,
     richMessage: Option[String] = None,
     attachment: Option[java.io.File] = None
     )

  def sendMail(mail: Mail): Unit = {
    val commonsMail = new SimpleEmail()
    commonsMail.setHostName("smtp.gmail.com")
    commonsMail.setAuthentication("scalasparkepita@gmail.com", "Azerty-1234")
    commonsMail.setTLS(true)
    mail.to foreach commonsMail.addTo
    mail.cc foreach commonsMail.addCc
    mail.bcc foreach commonsMail.addBcc

    commonsMail
    .setMsg(mail.message)
    .setFrom(mail.from._1, mail.from._2)
    .setSubject(mail.subject)
    .send()
  }
}
