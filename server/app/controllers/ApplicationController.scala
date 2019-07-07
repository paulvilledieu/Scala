package controllers

import javax.inject._
import models.TimeStats
import play.api.Logging
import play.api.mvc._
import services.StatsService
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ApplicationController @Inject()(cc: ControllerComponents, globalService: StatsService) extends AbstractController(cc) with Logging {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    globalService.listAllTimes map { globals =>
      Ok(views.html.index(globals))
    }
  }
}
