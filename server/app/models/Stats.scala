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

class Stats @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  val timeStats = TableQuery[TimeTableDef]

  def listAllTimes: Future[Seq[TimeStats]] = {
    dbConfig.db.run(timeStats.result)
  }
}
