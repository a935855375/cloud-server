package com.example.demo.repostory

import com.example.demo.models.FileList
import org.springframework.data.jpa.repository.{JpaRepository, Query}
import org.springframework.stereotype.Repository

@Repository
trait FileListRepository extends JpaRepository[FileList, Int] {

  @Query(value = "select * from file_list u where u.md5 = :md5", nativeQuery = true)
  def getFileByMd5(md5: String): java.util.List[FileList]
}
