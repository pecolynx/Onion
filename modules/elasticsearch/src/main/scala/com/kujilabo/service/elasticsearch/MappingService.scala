package com.kujilabo.service.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import com.kujilabo.common._
import com.kujilabo.config.CommonConstants
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.db.{DbField, DbMapping}
import com.kujilabo.models.elasticsearch.es.{EsField, EsFieldList, EsMapping}
import com.kujilabo.models.elasticsearch._
import com.kujilabo.service.elasticsearch.db.{DbFieldService, DbIndexService, DbMappingService}
import com.kujilabo.service.elasticsearch.es.EsMappingService

object MappingService extends LazyLogging {

  def getMapping(esUrl: String, indexName: IndexName, mappingName: MappingName): Mapping = {
    val index = IndexService.getIndex(esUrl, indexName)
    val dbMapping = DbMappingService.getModelByKey(index.id, mappingName)
    val esMapping = EsMappingService.getMapping(esUrl, indexName, mappingName)
    return new Mapping(dbMapping.id, dbMapping.version, index, mappingName)
  }

  def addMapping(esUrl: String, indexName: IndexName, mapping: Mapping): Unit = {
    val index = IndexService.getIndex(esUrl, indexName)
    val containsDb = DbMappingService.containsModel(index.id, mapping.name)
    val containsEs = EsMappingService.containsMapping(esUrl, indexName, mapping.name)

    if (containsDb) {
      logger.warn("db mapping already exists.")
      logger.warn("{ name : " + mapping.name + " }")
    }

    if (containsEs) {
      logger.warn("es mapping already exists.")
      logger.warn("{ name : " + mapping.name + " }")
    }

    if (containsDb || containsEs) {
      throw new ModelAlreadyExistsException(" name : " + mapping.name + " }")
    }
    try {
      DbMappingService.addModel(new DbMapping(index.id, mapping.name))

      val esMapping = new EsMapping(
        mapping.name,
        new EsFieldList(List.empty[EsField]))
      EsMappingService.addMapping(esUrl, index.name, esMapping)
    }
    catch {
      // ロールバック
      case ex: Exception =>

    }
  }

  def updateMapping(esUrl: String, index: Index, mapping: Mapping, userFieldList: FieldList): Unit = {
    // Dbの更新
    val dbMapping = DbMappingService.getModelByKey(index.id, mapping.name)
    userFieldList.list.foreach { x =>
      val contains = DbFieldService.contains(dbMapping.id, x.esName)
      if (!contains) {
        DbFieldService.addModel(
          new DbField(x.esName, x.dbFieldType.id, x.dbSeq, dbMapping.id, x.dbMultiLangName),
          new ModelIdImplT[Int](CommonConstants.LANG_ID_JA)
        )
      }
      else {
        val dbField = DbFieldService.getModelByKey(dbMapping.id, x.esName)
        DbFieldService.updateModel(new DbField(dbField.id, dbField.version, dbField.createdAt, dbField.updatedAt,
          dbField.name, dbField.fieldType, x.dbSeq, dbField.mappingId, x.dbMultiLangName))
      }
    }

    // Esの更新

    val esUserFieldList = userFieldList.toEsFieldList
    val esMapping = new EsMapping(mapping.name, esUserFieldList)
    EsMappingService.updateMapping(esUrl, index.name, esMapping)

  }

  def containsMapping(esUrl: String, indexName: IndexName, mappingName: MappingName): Boolean = {
    val dbIndex = DbIndexService.getModelByKey(indexName)
    val containsDb = DbMappingService.containsModel(dbIndex.id, mappingName)
    val containsEs = EsMappingService.containsMapping(esUrl, indexName, mappingName)

    containsDb && containsEs
  }

  def getFieldList(esUrl: String, mapping: Mapping): FieldList = {
    val dbFieldList = DbFieldService.getModelList(mapping.index.id)
    return null
  }
}
