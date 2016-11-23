package service.elasticsearch.es

import models.elasticsearch.es.EsIndex
import models.exceptions.ModelNotFoundException
import models.elasticsearch.{ElasticSearchIndexSetting, IndexName}
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import service.{HttpClientUtils, RestClient}

object EsIndexService {
  def containsIndex(esUrl: String, indexName: IndexName): Boolean = {
    val url = esUrl + "/" + indexName
    try {
      val response = RestClient.head(url)
      if (response.getStatusCode() == HttpStatus.OK) {
        println(" contains")
        return true
      }

      throw new RuntimeException()
    }
    catch {
      case ex: HttpClientErrorException =>
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
          println("not contains")
          return false
        }

        throw ex
    }
  }

  def getIndex(esUrl: String, indexName: IndexName): EsIndex = {
    if (containsIndex(esUrl, indexName)) {
      return new EsIndex(indexName)
    }

    throw new ModelNotFoundException(indexName.value.value)
  }

  def addIndex(esUrl: String, indexName: IndexName): Unit = {
    val settings = new ElasticSearchIndexSetting().get()
    val url = esUrl + "/" + indexName
    try {
      RestClient.post(url, settings)
    }
    catch {
      case ex: HttpClientErrorException =>
        println(ex.getMessage)
        println(ex.getResponseBodyAsString)
        throw ex
    }
    println("add2")
  }

  def removeIndex(esUrl: String, indexName: IndexName): Unit = {
    val url = esUrl + "/" + indexName + "/"
    println("delete : " + url)
    try {
      RestClient.delete(url, "")
    }
    catch {
      case ex: HttpClientErrorException =>
        if (HttpClientUtils.isNotFound(ex)) {
          throw new ModelNotFoundException("{ name " + indexName + " }")
        }
    }
  }
}
