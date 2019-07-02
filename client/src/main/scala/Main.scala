import java.io.File

import scala.io.Source
import FileParser.{getDirFiles, parseFiles}

import scala.concurrent.Await
import scala.concurrent.duration._

object Main extends App {


  def readLines(file: File) = {
    if (file.exists && file.canRead) {
      Right(Source.fromFile(file).getLines().toList)
    } else {
      Left("file does not exist")
    }
  }

  getDirFiles("./data/", List(".csv", ".json")) match {
    case Left(x) => x
    case Right(x) =>
      val future = parseFiles(x)
      Await.result(future, Duration.Inf)
  }
}
