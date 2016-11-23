package service.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import models.elasticsearch.db.DbIndex
import models.elasticsearch.{Index, IndexName}
import models.exceptions.{ModelAlreadyExistsException, ModelNotFoundException}
import service.elasticsearch.db.DbIndexService
import service.elasticsearch.es.EsIndexService

/**
  * インデックスサービス。
  */
object IndexService extends LazyLogging {
  def getIndex(esUrl: String, indexName: IndexName): Index = {
    val dbIndex = DbIndexService.getModelByKey(indexName)
    val esIndex = EsIndexService.getIndex(esUrl, indexName)
    return new Index(dbIndex.id, dbIndex.version, indexName)
  }

  /**
    * インデックスを追加します。
    *
    * @param esUrl ElasticSearchUrl
    * @param index インデックス
    */
  def addIndex(esUrl: String, index: Index): Unit = {
    val containsDb = DbIndexService.containsModel(index.name)
    val containsEs = EsIndexService.containsIndex(esUrl, index.name)

    if (containsDb) {
      logger.warn("db index already exists.")
      logger.warn("{ name : " + index.name + " }")
    }

    if (containsEs) {
      logger.warn("es index already exists.")
      logger.warn("{ name : " + index.name + " }")
    }

    if (containsDb || containsEs) {
      throw new ModelAlreadyExistsException(" name : " + index.name + " }")
    }

    try {
      DbIndexService.addModel(new DbIndex(index.name))
      EsIndexService.addIndex(esUrl, index.name)
    }
    catch {
      // ロールバック
      case ex: Exception =>

    }
  }

  /**
    * インデックスを削除します。
    *
    * @param esUrl ElasticSearchUrl
    * @param index インデックス
    */
  def removeIndex(esUrl: String, index: Index): Unit = {
    val containsDb = DbIndexService.containsModel(index.name)
    val containsEs = EsIndexService.containsIndex(esUrl, index.name)

    if (!containsDb) {
      logger.warn("db index not found.")
      logger.warn("{ name : " + index.name + " }")
    }

    if (!containsEs) {
      logger.warn("es index not found.")
      logger.warn("{ name : " + index.name + " }")
    }

    if (!containsDb || !containsEs) {
      throw new ModelNotFoundException(" name : " + index.name + " }")
    }

    try {
      DbIndexService.removeModel(index.id, index.version)
      EsIndexService.removeIndex(esUrl, index.name)
    }
    catch {
      // ロールバック
      case ex: Exception =>

    }
  }


  def containsIndex(esUrl: String, indexName: IndexName): Boolean = {
    val containsDb = DbIndexService.containsModel(indexName)
    val containsEs = EsIndexService.containsIndex(esUrl, indexName)

    containsDb && containsEs
  }
}
