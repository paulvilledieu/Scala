import java.io.File

import scala.io.Source
import Sender.{Message, send_msg, csv_parser, json_parser}

import scala.concurrent.{ExecutionContext, Future}

object FileParser {

  def getDirFiles(path: String, extensions: List[String]): Either[String, List[File]] = {
    path match {
      case "" => Left("Invalid path")
      case _ => getListOfFiles(new File(path), extensions)
    }
  }

  def parseFiles(files: List[File]) = {
    val futures = files.map(file => file.getName match {
      case name if name.endsWith(".csv") =>
        parseFile(file, csv_parser)

      case name if name.endsWith(".json") =>
        parseFile(file, json_parser)
    })
    implicit val context: ExecutionContext = ExecutionContext.Implicits.global
    Future.sequence(futures)
  }


  private[this]
  def getListOfFiles(dir: File, extensions: List[String]) = {
    dir match {
      case d if d.exists() && d.isDirectory =>
        Right(dir.listFiles
              .filter(_.isFile)
              .filter(_.canRead)
              .filter(file => extensions.exists(file.getName.endsWith(_)))
              .toList
        )
      case _ =>
        Left("dir does not exist or is not a directory")
    }
  }

  private[this]
  def parseFile(file: File,
                f_parser: Function[String, Message]) = {

    implicit val context: ExecutionContext = ExecutionContext.Implicits.global
    val futures = Source.fromFile(file)
                  .getLines
                  .map({ line =>
                    Future {
                      send_msg(f_parser(line))
                    }
                  })
    Future.sequence(futures)

  }
}
