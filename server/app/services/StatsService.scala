package services

import java.sql.Date
import java.time.LocalDate

import com.google.inject.Inject
import models.{Battery, Stats, Temperature, TimeStats}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

class StatsService @Inject()(stats: Stats) {

  implicit val context = ExecutionContext.Implicits.global

  def listAllTimes: Future[Seq[TimeStats]] = {
    stats.listAllTimes
  /*
    val date = Date.valueOf(LocalDate.now())
    Future(Seq(
      TimeStats(date, 100, Some(13), Some(10), Some(3)),
      TimeStats(date, 100, Some(12), Some(9), Some(3)),
      TimeStats(date, 100, Some(10), Some(5), Some(5)),
      TimeStats(date, 100, Some(13), Some(7), Some(6)),
      TimeStats(date, 99, Some(14), Some(9), Some(5)),
      TimeStats(date, 100, Some(13), Some(8), Some(5)),
      TimeStats(date, 100, Some(11), Some(7), Some(4))))
  */
  }

  def listAllTemperature: Future[Seq[Temperature]] = {
    stats.listAllTemperature
  }

  def listAllBattery: Future[Seq[Battery]] = {
    stats.listAllBattery
  }

  def getAll ={
    (listAllTimes, listAllTemperature, listAllBattery)
  }
}
