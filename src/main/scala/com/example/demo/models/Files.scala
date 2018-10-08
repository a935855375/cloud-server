package com.example.demo.models

import scala.beans.BeanProperty

class Files {
  @BeanProperty
  var isFold: Boolean = _
  @BeanProperty
  var name: String = _
  @BeanProperty
  var md5: String = _
  @BeanProperty
  var date: String = _
  @BeanProperty
  var size: Long = _
  @BeanProperty
  var UUID: String = _
  @BeanProperty
  var `type`: String = _
  @BeanProperty
  var files: Array[Files] = _
}