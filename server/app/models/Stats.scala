package models

import java.sql.Date

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.MySQLProfile.api._


case class TimeStats(date: Date, nbConnectedObject: Int, nbPanne: Option[Int], nbPanneNorth: Option[Int], nbPanneSouth: Option[Int])

class TimeTableDef(tag: Tag) extends Table[TimeStats](tag, "globals") {
  def date = column[Date]("date", O.PrimaryKey)
  def nbConnectedObject= column[Int]("nbConnectedObject")
  def nbPanne = column[Option[Int]]("nbPanne")
  def nbPanneNorth = column[Option[Int]]("nbPanneNorth")
  def nbPanneSouth = column[Option[Int]]("nbPanneSouth")

  override def * =
    (date, nbConnectedObject, nbPanne, nbPanneNorth, nbPanneSouth) <> (TimeStats.tupled, TimeStats.unapply)
}

case class Temperature(temperature: Int, nbPanne: Int)

class TemperatureDef(tag: Tag) extends Table[Temperature](tag, "temperature") {
  def temperature = column[Int]("temperature", O.PrimaryKey)
  def nbPanne = column[Int]("nbPanne")

  override def * =
    (temperature, nbPanne) <> (Temperature.tupled, Temperature.unapply)
}

case class Battery(battery: Int, nbPanne: Int)

class BatteryTableDef(tag: Tag) extends Table[Battery](tag, "battery") {
  def battery = column[Int]("battery", O.PrimaryKey)
  def nbPanne = column[Int]("nbPanne")

  override def * =
    (battery, nbPanne) <> (Battery.tupled, Battery.unapply)
}


class Stats @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val timeStats = TableQuery[TimeTableDef]
  val temperature = TableQuery[TemperatureDef]
  val battery = TableQuery[BatteryTableDef]

  def listAllTemperature: Future[Seq[Temperature]] = {
    dbConfig.db.run(temperature.result)
  }

  def listAllBattery: Future[Seq[Battery]] = {
    dbConfig.db.run(battery.result)
  }

  def listAllTimes: Future[Seq[TimeStats]] = {
    dbConfig.db.run(timeStats.result)
  }
}
