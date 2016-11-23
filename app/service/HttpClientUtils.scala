package service

import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException

object HttpClientUtils {
  def isNotFound(ex: HttpClientErrorException): Boolean = {
    ex.getStatusCode() == HttpStatus.NOT_FOUND
  }
}
