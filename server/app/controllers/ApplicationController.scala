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
    val timestamp = (json \ "timestamp").as[Long]
    val objectId = (json \ "objectId").as[String]
    val latitude = (json \ "latitude").as[Double]
    val longitude = (json \ "longitude").as[Double]
    val temperature = (json \ "temperature").as[Float]
    val batteryRemaining = (json \ "batteryRemaining").as[Int]
    val heartRate = (json \ "heartRate").as[Float]
    val state = (json \ "state").as[String]
    val message = (json \ "message").as[String]
    val data = Data(1, timestamp, objectId, latitude, longitude, temperature, batteryRemaining, heartRate, state, message, false)
    dataService.addData(data).map( _ => Redirect(routes.ApplicationController.index()))
  }

  def addAlert() = Action.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val timestamp = (json \ "timestamp").as[Long]
    val objectId = (json \ "objectId").as[String]
    val latitude = (json \ "latitude").as[Double]
    val longitude = (json \ "longitude").as[Double]
    val temperature = (json \ "temperature").as[Float]
    val batteryRemaining = (json \ "batteryRemaining").as[Int]
    val heartRate = (json \ "heartRate").as[Float]
    val state = (json \ "state").as[String]
    val message = (json \ "message").as[String]
    val data = Data(1, timestamp, objectId, latitude, longitude, temperature, batteryRemaining, heartRate, state, message, true)
    dataService.addData(data).map( _ => Redirect(routes.ApplicationController.index()))
  }

  def deleteData(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    dataService.deleteData(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}
