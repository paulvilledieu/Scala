package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Data(id: Long, objectId: Long, latitude: String, longitude: String, temperature: String, batteryRemaining: String, heartRate: String)

import slick.jdbc.MySQLProfile.api._

class DataTableDef(tag: Tag) extends Table[Data](tag, "data") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def objectId = column[Long]("objectId")
  def latitude = column[String]("latitude")
  def longitude = column[String]("longitude")
  def temperature = column[String]("temperature")
  def batteryRemaining = column[String]("batteryRemaining")
  def heartRate = column[String]("heartRate")

  override def * =
    (id, objectId, latitude, longitude, temperature, batteryRemaining, heartRate) <>(Data.tupled, Data.unapply)
}

class Datas @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val datas = TableQuery[DataTableDef]

  def add(data: Data): Future[String] = {
    dbConfig.db.run(datas += data).map(res => "Data successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(datas.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Data]] = {
    dbConfig.db.run(datas.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Data]] = {
   dbConfig.db.run(datas.result)
  }

}