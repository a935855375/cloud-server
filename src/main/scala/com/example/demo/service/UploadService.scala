package com.example.demo.service

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import com.example.demo.models.{Files, UserFiles}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.stereotype.Service

@Service
class UploadService @Autowired()(mongo: MongoTemplate) {

  def saveFileToUserDir(path: String, fileName: String, md5: String, size: Int): Files = {
    val node = path.split(",")

    val query = new Query(Criteria.where("_id").is(1))

    val tree = mongo.findOne(query, classOf[UserFiles])

    var source: Files = tree.getFile

    source.size = source.size + size

    if (source.UUID != path)
      node.tail.foreach { next =>
        source = source.files.find(_.UUID == next).get
        source.size = source.size + size
      }

    val file = new Files
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    file.isFold = false
    file.name = fileName
    file.date = format.format(new Date)
    file.UUID = UUID.randomUUID().toString.replace("-", "").toLowerCase
    file.md5 = md5
    file.size = size
    file.`type` = getFileType(fileName)

    source.files = source.files ++ Array(file)

    mongo.save(tree)

    file
  }

  def renameFile(path: String, uuid: String, name: String): Unit = {
    val node = path.split(",")

    val query = new Query(Criteria.where("_id").is(1))

    val tree = mongo.findOne(query, classOf[UserFiles])

    var source: Files = tree.getFile

    if (source.UUID != path)
      node.tail.foreach(next => source = source.files.find(_.UUID == next).get)

    val idx = source.files.indexWhere(_.UUID == uuid)

    source.files(idx).name = name

    mongo.save(tree)
  }

  def newFolder(path: String, name: String): Files = {
    val node = path.split(",")

    val query = new Query(Criteria.where("_id").is(1))

    val tree = mongo.findOne(query, classOf[UserFiles])

    var source: Files = tree.getFile

    if (source.UUID != path)
      node.tail.foreach(next => source = source.files.find(_.UUID == next).get)

    val file = new Files

    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    file.isFold = true
    file.name = name
    file.UUID = UUID.randomUUID().toString.replace("-", "").toLowerCase
    file.date = format.format(new Date)
    file.size = 0
    file.files = Array()

    source.files = source.files ++ Array(file)

    mongo.save(tree)

    file
  }

  def deleteFile(path: String, uuid: String): Unit = {
    val node = path.split(",")

    val query = new Query(Criteria.where("_id").is(1))

    val tree = mongo.findOne(query, classOf[UserFiles])

    var source: Files = tree.getFile

    if (source.UUID != path)
      node.tail.foreach(next => source = source.files.find(_.UUID == next).get)

    val ids = uuid.split(",").toSet

    val toRemove = source.files.filter(x => ids(x.UUID))

    val size = toRemove.map(_.size).sum

    source.files = source.files.diff(toRemove)

    source = tree.getFile

    source.size = source.size - size

    node.tail.foreach { next =>
      source = source.files.find(_.UUID == next).get
      source.size = source.size - size
    }

    mongo.save(tree)
  }

  def getFileType(fileName: String): String = {
    import UploadService._
    extractFileSuffix(fileName) match {
      case x: String if zip.contains(x) => "zip"
      case x: String if txt.contains(x) => "txt"
      case x: String if doc.contains(x) => "doc"
      case x: String if pic.contains(x) => "pic"
      case x: String if video.contains(x) => "video"
      case x: String if audio.contains(x) => "audio"
      case x: String if code.contains(x) => "code"
      case "pdf" => "pdf"
      case "apk" => "apk"
      case _ => "other"
    }
  }

  def extractFileSuffix(fileName: String): String = fileName.lastIndexOf(".") match {
    case -1 => ""
    case point: Int => fileName.substring(point + 1).toLowerCase
  }
}

object UploadService {
  val zip = List("zip", "rar", "7z")
  val txt = List("txt", "json")
  val doc = List("doc, docx")
  val pic = List("png", "jpg", "jpeg", "gif", "svg")
  val video = List("mp4", "avi", "mov", "rmvb", "3gp", "wmv", "mpeg", "mkv", "flv", "vob")
  val audio = List("mp3", "wav", "wma", "ogg", "ape", "acc")
  val code = List("java", "scala", "c", "cpp", "py", "sc", "php", "kt", "sh", "bat", "bash", "css", "html", "js", "ts", "h")
}