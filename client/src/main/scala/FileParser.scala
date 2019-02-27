package tools

import java.io.File

object FileParser {

  def getDirFiles(path: String, extensions: List[String]): Either[String, List[File]] = {
    path match {
      case "" => Left("Invalid path")
      case _ => getListOfFiles(new File(path), extensions)
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


}
