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

  def deleteData(id: Long) = Action.async { implicit request: Request[AnyContent] =>
    dataService.deleteData(id) map { res =>
      Redirect(routes.ApplicationController.index())
    }
  }

}
