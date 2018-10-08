package com.example.demo.config

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.cors.{CorsConfiguration, UrlBasedCorsConfigurationSource}
import org.springframework.web.filter.CorsFilter

@Configuration
class CorsConfig {
  def buildConfig(): CorsConfiguration = {
    val corsConfig = new CorsConfiguration
    corsConfig.addAllowedOrigin("*")
    corsConfig.addAllowedHeader("*")
    corsConfig.addAllowedMethod("*")
    corsConfig
  }

  @Bean
  def corsFilter(): CorsFilter = {
    val source = new UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", buildConfig())
    new CorsFilter(source)
  }
}
