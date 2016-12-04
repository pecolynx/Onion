package com.kujilabo.service.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.{IndexName, MappingName}
import com.kujilabo.models.elasticsearch.es.EsDocument
import com.kujilabo.service.elasticsearch.es.EsDocumentService

object DocumentService extends LazyLogging {

  def addDocument(esUrl: String, esDocument: EsDocument): Option[ModelIdT[String]] = {
    EsDocumentService.addDocument(esUrl, esDocument)
  }

  def removeDocument(esUrl: String, indexName: IndexName, mappingName: MappingName,
                     id: ModelIdT[String]): Unit = {
    EsDocumentService.removeDocument(esUrl, indexName, mappingName, id)
  }

  def removeDocumentByUser(esUrl: String, indexName: IndexName, mappingName: MappingName,
                           appUserId: ModelIdT[Int]): Unit = {
    EsDocumentService.removeDocumentByUser(esUrl, indexName, mappingName, appUserId)
  }

  def updateDocument(esUrl: String, esDocument: EsDocument): Unit = {
    EsDocumentService.updateDocument(esUrl, esDocument)
  }

  def getDocument(esUrl: String, indexName: IndexName, mappingName: MappingName,
                  id: ModelIdT[String]): Option[EsDocument] = {
    EsDocumentService.getDocumentById(esUrl, indexName, mappingName, id)
  }
}
