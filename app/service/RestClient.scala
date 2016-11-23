package service

import org.springframework.http.{HttpEntity, HttpHeaders, HttpMethod, ResponseEntity}
import org.springframework.web.client.RestTemplate


object RestClient {

  val rest = new RestTemplate()
  val headers = this.createHttpHeaders()

  def get(url: String, json: String): ResponseEntity[String] = {
    val requestEntity = new HttpEntity[String](json, this.headers)
    this.rest.exchange(url, HttpMethod.GET, requestEntity, classOf[String])
  }

  def post(url: String, json: String): ResponseEntity[String] = {
    val requestEntity = new HttpEntity[String](json, this.headers)
    this.rest.exchange(url, HttpMethod.POST, requestEntity, classOf[String])
  }

  def put(url: String, json: String): ResponseEntity[String] = {
    val requestEntity = new HttpEntity[String](json, this.headers)
    this.rest.exchange(url, HttpMethod.PUT, requestEntity, classOf[String])
  }

  def delete(url: String, json: String): ResponseEntity[String] = {
    val requestEntity = new HttpEntity[String](json, this.headers)
    this.rest.exchange(url, HttpMethod.DELETE, requestEntity, classOf[String])
  }

  def head(url: String): ResponseEntity[String] = {
    val requestEntity = new HttpEntity[String](this.headers)
    this.rest.exchange(url, HttpMethod.HEAD, requestEntity, classOf[String])
  }

  private def createHttpHeaders(): HttpHeaders = {
    val headers = new HttpHeaders()
    headers.add("Content-Type", "application/json; charset=UTF-8")
    headers.add("Accept", "*/*")
    return headers
  }
}
