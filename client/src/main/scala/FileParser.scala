import java.io.File

import scala.io.Source
import Sender.{Message, send_msg}

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

object FileParser {

  def csv_parser(str: String): Message  =  {
    Message(0, "")
  }


  def json_parser(str: String): Message = {
    Message(0, "")
  }

  def getDirFiles(path: String, extensions: List[String]): Either[String, List[File]] = {
    path match {
      case "" => Left("Invalid path")
      case _ => getListOfFiles(new File(path), extensions)
    }
  }

  def parseFiles(files: List[File]): Unit = {
    files match {
      case Nil =>
      case file :: rest => file.getName match {
          case name if name.endsWith(".csv") =>
            parseFile(file, csv_parser)

          case name if name.endsWith(".json") =>
            parseFile(file, json_parser)
        }
        parseFiles(rest)
    }
  }


  private[this]
  def getListOfFiles(dir: File, extensions: List[String]): Either[String, List[File]] = {
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
                f_parser: Function[String, Message]): Unit = {
    Source.fromFile(file)
      .getLines
      .foreach { line =>
        val future: Future[String] = Future {
          send_msg(f_parser(line))
        }
      }
  }

}
