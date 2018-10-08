package com.example.demo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DemoApplication

object DemoApplication extends App {
  SpringApplication.run(classOf[DemoApplication], args: _*)
}

