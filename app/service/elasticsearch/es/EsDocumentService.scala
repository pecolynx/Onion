package service.elasticsearch.es

import com.typesafe.scalalogging.LazyLogging
import models.exceptions.{DocumentNotFoundException, ModelNotFoundException}
import models._
import com.kujilabo.models.core._
import models.elasticsearch._
import models.elasticsearch.es.{EsDocument, EsDocumentBuilder, EsDocumentFieldFactory}
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import service.RestClient
import utils.{JsonUtils, ResponseUtil}

object EsDocumentService extends LazyLogging {

  def getDocumentById(esUrl: String, indexName: IndexName, mappingName: MappingName,
                      id: ModelIdT[String]): Option[EsDocument] = {

    val url = esUrl + "/" + indexName + "/" + mappingName + "/" + id

    logger.warn(url)
    def getJson(url: String): Option[String] = {
      try {
        Some(RestClient.get(url, "").getBody())
      }
      catch {
        case ex: HttpClientErrorException =>
          if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            //throw new DocumentNotFoundException("documentId : " + id.toString, id.toString)
            return None
          }

          return None
      }

    }
    val response = getJson(url)
    if (response.isEmpty) {
      return None
    }

    logger.warn(response.get)
    val map = JsonUtils.toMap(response.get)

    val esMapping = EsMappingService.getMapping(esUrl, indexName, mappingName)
    val esFieldList = esMapping.fieldList

    val tagCollection = new TagCollection(new TagList(List.empty[Tag]), new TagList(List.empty[Tag]))

    for {
      version <- ResponseUtil.getVersion(map)
      source <- ResponseUtil.getSource(map)
      systemFieldList <- ResponseUtil.createSystemField(source)
    } yield {
      val userDocumentFieldList = EsDocumentBuilder.buildDocumentFieldList(source, esFieldList)
      new EsDocument(
        id, version, systemFieldList.createdAt, systemFieldList.updatedAt,
        indexName, mappingName, systemFieldList.title, systemFieldList.createdBy,
        userDocumentFieldList)
    }
  }

  def addDocument(esUrl: String, document: EsDocument): Option[ModelIdT[String]] = {
    val indexName = document.indexName
    val mappingName = document.mappingName
    val url = esUrl + "/" + indexName + "/" + mappingName
    val map = document.map
    val json = JsonUtils.toJson(map)

    try {
      val response1 = RestClient.post(url, json)
      val response2 = ResponseUtil.createResponseOfDocumentAddFromJson(response1.getBody())
      response2.map((x) => x.id)
    }
    catch {
      case ex: HttpClientErrorException =>
        logger.warn(ex.getResponseBodyAsString)
        return None
    }
  }

  def removeDocument(esUrl: String, indexName: IndexName, mappingName: MappingName,
                     id: ModelIdT[String]): Unit = {

    val url = esUrl + "/" + indexName + "/" + mappingName + "/" + id
    logger.warn("remove document. url : " + url)
    try {
      val response = RestClient.delete(url, "")
      logger.warn("removed. response : " + response)
    }
    catch {
      case ex: HttpClientErrorException =>
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
          throw new ModelNotFoundException(id.toString())
        }

        logger.warn(ex.getResponseBodyAsString)
        throw ex
    }
  }

  def removeDocumentByUser(esUrl: String, indexName: IndexName, mappingName: MappingName,
                           appUserId: ModelIdT[Int]): Unit = {

    val url = esUrl + "/" + indexName + "/" + mappingName + "/_query?q=created_by:" + appUserId
    try {
      RestClient.delete(url, "")
    }
    catch {
      case ex: HttpClientErrorException =>
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
          throw new ModelNotFoundException(appUserId.toString())
        }

        logger.warn(ex.getResponseBodyAsString)
        throw ex
    }
  }

  def updateDocument(esUrl: String, document: EsDocument): Unit = {
    val indexName = document.indexName
    val mappingName = document.mappingName
    //val url = esUrl + "/" + indexName + "/" + mappingName + "/" + document.id + "/_update"
    val url = esUrl + "/" + indexName + "/" + mappingName + "/" + document.id
    val map = document.map
    val json = JsonUtils.toJson(map)

    try {
      RestClient.post(url, json)
    }
    catch {
      case ex: HttpClientErrorException =>
        logger.warn(ex.getResponseBodyAsString)
        throw ex
    }

  }

  def containsDocument(esUrl: String, indexName: IndexName, mappingName: MappingName,
                       id: ModelIdT[String]): Boolean = {

    val url = esUrl + "/" + indexName + "/" + mappingName + "/" + id
    try {
      RestClient.head(url)
      return true
    }
    catch {
      case ex: HttpClientErrorException =>
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
          return false
        }

        logger.warn(ex.getResponseBodyAsString)
        throw ex
    }
  }
}
