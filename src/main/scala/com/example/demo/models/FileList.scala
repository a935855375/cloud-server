package com.example.demo.models

import java.util.Date

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "file_list", uniqueConstraints = Array(new UniqueConstraint(columnNames = Array("md5"))))
class FileList {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(nullable = false)
  @BeanProperty
  var md5: String = _

  @Column(nullable = false)
  @BeanProperty
  var Date: Date = _

}
