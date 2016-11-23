package service.elasticsearch.es

import java.util.NoSuchElementException

import com.typesafe.scalalogging.LazyLogging
import models.exceptions.ModelNotFoundException
import models.core.VariableName
import models.elasticsearch.es._
import models.elasticsearch.{IndexName, MappingName}
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.client.HttpClientErrorException
import service.RestClient
import utils.JsonUtils

object EsMappingService extends LazyLogging {

  /**
    *
    * @param esUrl
    * @param indexName
    * @param mappingName
    * @return
    */
  def getMapping(esUrl: String, indexName: IndexName, mappingName: MappingName): EsMapping = {
    val url = esUrl + "/" + indexName + "/_mapping/" + mappingName

    def function(): String = {
      val body = RestClient.get(url, "").getBody
      if (body == "{}") {
        throw new ModelNotFoundException(mappingName.toString())
      }

      return body
    }

    def exceptionFunction(ex: HttpClientErrorException): String = processException(ex, mappingName)

    val json = this.executeHttp[String](function, exceptionFunction)

    val map = JsonUtils.toMap(json)

    val properties = this.getProperties(map, indexName, mappingName)

    var userFieldList = properties.map { x =>
      this.createField(x._1, x._2.asInstanceOf[Map[String, Object]])
    }.toList

    return new EsMapping(mappingName, new EsFieldList(userFieldList))


  }

  def getProperties(map: Map[String, Any], indexName: IndexName, mappingName: MappingName): Map[String, Object] = {

    val index = map.get(indexName.value.value).get.asInstanceOf[Map[String, Object]]
    println("index = " + index)

    val mappings = index.get("mappings").get.asInstanceOf[Map[String, Object]]
    println("mappings = " + mappings)

    val mapping = mappings.get(mappingName.value.value).get.asInstanceOf[Map[String, Object]]
    println("mapping = " + mapping)

    val properties = mapping.get("properties").get.asInstanceOf[Map[String, Object]]
    println("properties = " + properties)

    return properties
  }

  def createField(name: String, propertyMap: Map[String, Object]): EsField = {
    try {
      val fieldType = propertyMap.get("type").get.asInstanceOf[String]
      val fieldFormat = propertyMap.get("format").getOrElse("").asInstanceOf[String]
      val fieldAnalyzer = propertyMap.get("analyzer").getOrElse("").asInstanceOf[String]
      return new EsField(new VariableName(name), true, true, true,
        EsFieldType.parse(fieldType),
        EsFieldFormat.parse(fieldFormat), EsFieldAnalyzer.parse(fieldAnalyzer))
    } catch {
      case ex: NoSuchElementException =>
        logger.warn("propertyMap : " + propertyMap.toString)
        throw ex
    }

  }

  def addMapping(esUrl: String, indexName: IndexName, mapping: EsMapping): Unit = {
    val json = JsonUtils.toJson(mapping.map)
    logger.info("json = " + json)
    this.putMapping(esUrl, indexName, mapping.name, json)
  }

  def updateMapping(esUrl: String, indexName: IndexName, mapping: EsMapping): Unit = {
    val json = JsonUtils.toJson(mapping.map)
    logger.info("json = " + json)
    this.putMapping(esUrl, indexName, mapping.name, json)
  }

  private def putMapping(esUrl: String, indexName: IndexName, mappingName: MappingName, json: String): Unit = {
    val url = esUrl + "/" + indexName + "/_mapping/" + mappingName

    def function(): Boolean = RestClient.put(url, json).getStatusCode == HttpStatus.OK
    def exceptionFunction(ex: HttpClientErrorException): Boolean = processException(ex, mappingName)

    this.executeHttp[Boolean](function, exceptionFunction)
  }

  def removeMapping(esUrl: String, indexName: IndexName, mappingName: MappingName): Unit = {
    val url = esUrl + "/" + indexName + "/" + mappingName

    logger.warn(url)

    def function(): Boolean = RestClient.delete(url, "").getStatusCode == HttpStatus.OK
    def exceptionFunction(ex: HttpClientErrorException): Boolean = processException(ex, mappingName)

    this.executeHttp[Boolean](function, exceptionFunction)
  }

  def containsMapping(esUrl: String, indexName: IndexName, mappingName: MappingName): Boolean = {
    val url = esUrl + "/" + indexName + "/" + mappingName

    def function(): Boolean = RestClient.head(url).getStatusCode == HttpStatus.OK

    this.executeHttp[Boolean](function, processExceptionReturnsFalseIfNotFound)
  }

  def processException[T](ex: HttpClientErrorException, mappingName: MappingName): T = {
    if (ex.getStatusCode == HttpStatus.NOT_FOUND) {
      throw new ModelNotFoundException("{ name : " + mappingName + " }")
    }

    throw ex
  }

  def processExceptionReturnsFalseIfNotFound(ex: HttpClientErrorException): Boolean = {
    if (ex.getStatusCode == HttpStatus.NOT_FOUND) {
      return false
    }

    throw ex
  }

  def executeHttp[T](function: Function0[T], exceptionFunction: Function1[HttpClientErrorException, T]): T = {
    try {
      return function()
    }
    catch {
      case ex: HttpClientErrorException =>
        return exceptionFunction(ex)
    }
  }

}
