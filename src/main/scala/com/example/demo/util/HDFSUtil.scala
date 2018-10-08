package com.example.demo.util

import java.io.OutputStream

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.io.IOUtils
import org.springframework.stereotype.Component

@Component
class HDFSUtil {

  def getFS: FileSystem = {
    System.setProperty("HADOOP_USER_NAME", "linux")
    val conf = new Configuration
    conf.set("fs.defaultFS", "hdfs://linux:8020")
    conf.set("mapred.remote.os", "Linux")
    FileSystem.get(conf)
  }

  def downloadFromHDFS(uri: String, fileName: String, localFileOutPut: OutputStream): Unit = {
    val fs = this.getFS

    val is = fs.open(new Path(uri + "/" + fileName))

    IOUtils.copyBytes(is, localFileOutPut, 8 * 1024, true)

    is.close()

    fs.close()
  }

}
