package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class Data(id: Option[Long], timestamp: Long, objectId: String, latitude: Double, longitude: Double, temperature: Float, batteryRemaining: Int, heartRate: Float, state: String, message: String, isAlert: Boolean)

import slick.jdbc.MySQLProfile.api._

class DataTableDef(tag: Tag) extends Table[Data](tag, "data") {

  def id = column[Long]("id", O.PrimaryKey,O.AutoInc)
  def timestamp = column[Long]("timestamp")
  def objectId = column[String]("objectId")
  def latitude = column[Double]("latitude")
  def longitude = column[Double]("longitude")
  def temperature = column[Float]("temperature")
  def batteryRemaining = column[Int]("batteryRemaining")
  def heartRate = column[Float]("heartRate")
  def state = column[String]("state")
  def message = column[String]("message")
  def isAlert = column[Boolean]("isAlert")
  def idx = index("obj_entry_unique", (timestamp, objectId), unique = true)

  override def * =
    (id.?, timestamp, objectId, latitude, longitude, temperature, batteryRemaining, heartRate, state, message, isAlert) <>(Data.tupled, Data.unapply)
}

class Datas @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val datas = TableQuery[DataTableDef]

  def add(data: Data): Future[String] = {
    dbConfig.db.run(datas += data).map(_ => "Data successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(datas.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Data]] = {
    dbConfig.db.run(datas.filter(_.id === id).result.headOption)
  }

  def safeAdd(data: Data): Future[String] = {
    dbConfig.db.run(datas.filter(d => d.objectId === data.objectId
      && d.timestamp === data.timestamp).result.headOption)
      .flatMap {
        case None => add(data)
        case _ => Future {
          "Data already in table"
        }
      }
  }

  def listAll: Future[Seq[Data]] = {
   dbConfig.db.run(datas.result)
  }

}
