package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core._

case class ResponseOfDocumentAdd
(
  indexName: IndexName,
  mappingName: MappingName,
  id: ModelIdT[String],
  version: Int
) {

}
