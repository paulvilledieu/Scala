package controllers

import javax.inject._
import models.{Data}
import play.api.Logging
import play.api.mvc._
import services.DataService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ApplicationController @Inject()(cc: ControllerComponents, dataService: DataService) extends AbstractController(cc) with Logging {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    dataService.listAllDatas map { datas =>
      Ok(views.html.index(datas))
    }
  }

  import play.api.libs.json.Json

  def addData() = Action.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val id = (json \ "id").as[String].toLong
    val timestamp = (json \ "timestamp").as[String].toLong
    val objectId = (json \ "objectId").as[String].toLong
    val latitude = (json \ "latitude").as[String]
    val longitude = (json \ "longitude").as[String]
    val temperature = (json \ "temperature").as[String]
    val batteryRemaining = (json \ "batteryRemaining").as[String]
    val heartRate = (json \ "heartRate").as[String]
    val state = (json \ "state").as[String]
    val message = (json \ "message").as[String]
    val isAlert = (json \ "isAlert").as[String].toBoolean
    val data = Data(id, timestamp, objectId, latitude, longitude, temperature, batteryRemaining, heartRate, state, message, isAlert)
    dataService.addData(data).map( _ => Redirect(routes.ApplicationController.index()))
  }

  def deleteData(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    dataService.deleteData(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}
