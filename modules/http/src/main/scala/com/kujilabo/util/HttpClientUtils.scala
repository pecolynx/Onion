package com.kujilabo.util


import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.client.HttpClientErrorException

object HttpClientUtils {
  def isOk(response: ResponseEntity[_]): Boolean = {
    response.getStatusCode() == HttpStatus.OK
  }

  def isNotFound(ex: HttpClientErrorException): Boolean = {
    ex.getStatusCode() == HttpStatus.NOT_FOUND
  }
}
