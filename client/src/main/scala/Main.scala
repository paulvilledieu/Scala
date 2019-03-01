import java.io.File

import scala.io.Source
import FileParser.{getDirFiles, parseFiles}


object Main extends App {


  def readLines(file: File) = {
    if (file.exists && file.canRead) {
      Right(Source.fromFile(file).getLines().toList)
    } else {
      Left("file does not exist")
    }
  }

  getDirFiles("./src/test/resources", List(".csv", ".json")) match {
    case Left(x) => x
    case Right(x) => parseFiles(x)
  }
}
