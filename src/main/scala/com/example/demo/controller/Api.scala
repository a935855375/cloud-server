package com.example.demo.controller

import java.io.BufferedInputStream
import java.text.SimpleDateFormat
import java.util.{Date, UUID}

import com.example.demo.entity.{Status, StatusWithFile}
import com.example.demo.models.{FileList, Files, UserFiles}
import com.example.demo.repostory.{FileListRepository, UserRepository}
import com.example.demo.service.UploadService
import com.example.demo.util.HDFSUtil
import javax.servlet.http.HttpServletResponse
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

import scala.util.{Failure, Success, Try}

@RestController
class Api @Autowired()(hdfsUtil: HDFSUtil,
                       userRepository: UserRepository,
                       fileListRepository: FileListRepository,
                       uploadService: UploadService,
                       mongo: MongoTemplate) {
  final val base_url = "/user/linux/"

  @GetMapping(Array("/"))
  def index(): String = "hello, GG"

  @PostMapping(Array("/upload"))
  def upload(@RequestParam("file") file: MultipartFile,
             @RequestParam("md5") md5: String,
             @RequestParam("path") path: String,
             @RequestParam("size") size: Int): StatusWithFile = {

    if (fileListRepository.getFileByMd5(md5).size == 0) {
      val fileSystem = hdfsUtil.getFS

      val path = new Path(md5)

      val outStream = if (fileSystem.exists(path)) fileSystem.append(path)
      else fileSystem.create(path, 1: Short)

      val in = new BufferedInputStream(file.getInputStream)

      IOUtils.copyBytes(in, outStream, 10 * 1024)

      outStream.hflush()
      outStream.close()
      fileSystem.close()

      val fileList = new FileList

      fileList.md5 = md5
      fileList.Date = new Date

      fileListRepository.save(fileList)
    }

    val f = uploadService.saveFileToUserDir(path, file.getOriginalFilename, md5, size)

    StatusWithFile(status = true, md5, f)
  }

  @GetMapping(Array("/download"))
  def download(@RequestParam("file") fileName: String,
               @RequestParam("md5") md5: String,
               rep: HttpServletResponse): Unit = {
    val fileSystem = hdfsUtil.getFS

    if (fileSystem.exists(new Path(base_url + md5))) {
      rep.setHeader("Cache-Control", "no-cache, no-store, must-revalidate")
      rep.setHeader("Pragma", "no-cache")
      rep.setHeader("Expires", "0")
      rep.setHeader("charset", "utf-8")
      rep.setContentType("application/force-download")
      rep.setHeader("Content-Transfer-Encoding", "binary")
      rep.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"")

      val os = rep.getOutputStream

      hdfsUtil.downloadFromHDFS(base_url, md5, os)
    }
  }

  @GetMapping(Array("/dirs"))
  def getDir: Files = {
    val query = new Query(Criteria.where("id").is(1))

    val user = mongo.findOne(query, classOf[UserFiles])

    user.getFile
  }

  @GetMapping(Array("/rename"))
  def renameFile(@RequestParam("path") path: String,
                 @RequestParam("uuid") uuid: String,
                 @RequestParam("filename") fileName: String): Status = {
    Try(uploadService.renameFile(path, uuid, fileName)) match {
      case Success(_) => Status(status = true, "重命名成功")
      case Failure(_) => Status(status = false, "重命名失败")
    }
  }

  @GetMapping(Array("/newFolder"))
  def newFolder(@RequestParam("path") path: String,
                @RequestParam("name") folderName: String): StatusWithFile = {
    Try(uploadService.newFolder(path, folderName)) match {
      case Success(file) => StatusWithFile(status = true, "新建文件夹成功", file)
      case Failure(_) => StatusWithFile(status = false, "新建文件夹失败", null)
    }
  }

  @GetMapping(Array("/delete"))
  def deleteFile(@RequestParam("path") path: String,
                 @RequestParam("uuid") uuid: String): Status = {
    Try(uploadService.deleteFile(path, uuid)) match {
      case Success(_) => Status(status = true, "删除文件成功")
      case Failure(_) => Status(status = false, "删除文件失败")
    }
  }

  @GetMapping(Array("/newUser"))
  def newUser(): Unit = {
    val root = new Files
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    root.isFold = true
    root.name = "全部"
    root.date = format.format(new Date)
    root.UUID = UUID.randomUUID().toString.replace("-", "").toLowerCase
    root.files = Array[Files]()

    val userFiles = new UserFiles
    userFiles.setId(1)
    userFiles.setFile(root)

    mongo.save(userFiles)
  }
}
