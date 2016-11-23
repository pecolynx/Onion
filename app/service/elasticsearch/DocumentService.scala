package service.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import models.core.ModelIdT
import models.elasticsearch.{IndexName, MappingName}
import models.elasticsearch.es.EsDocument
import service.elasticsearch.es.EsDocumentService

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
