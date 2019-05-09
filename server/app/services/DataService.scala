package services

import com.google.inject.Inject
import models.{Data, Datas}

import scala.concurrent.Future

class DataService @Inject() (datas: Datas) {

  def addData(data: Data): Future[String] = {
    datas.add(data)
  }

  def deleteData(id: Long): Future[Int] = {
    datas.delete(id)
  }

  def getData(id: Long): Future[Option[Data]] = {
    datas.get(id)
  }

  def listAllDatas: Future[Seq[Data]] = {
    datas.listAll
  }
}
