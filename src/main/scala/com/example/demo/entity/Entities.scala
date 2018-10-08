package com.example.demo.entity

import com.example.demo.models.Files

import scala.beans.BeanProperty

case class Status(@BeanProperty status: Boolean, @BeanProperty message: String)

case class StatusWithFile(@BeanProperty status: Boolean, @BeanProperty message: String, @BeanProperty file: Files)