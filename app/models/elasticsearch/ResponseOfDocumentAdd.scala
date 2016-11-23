package models.elasticsearch

import models.core.ModelIdT

case class ResponseOfDocumentAdd
(
  indexName: IndexName,
  mappingName: MappingName,
  id: ModelIdT[String],
  version: Int
) {

}
