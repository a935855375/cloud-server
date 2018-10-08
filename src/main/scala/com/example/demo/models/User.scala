package com.example.demo.models

import javax.persistence._

import scala.beans.BeanProperty

@Entity
@Table(name = "user")
class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: Int = _

  @Column(nullable = false)
  @BeanProperty
  var username: String = _


}
