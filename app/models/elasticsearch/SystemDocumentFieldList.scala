package models.elasticsearch

import models.core.Title
import org.joda.time.DateTime

class SystemDocumentFieldList
(
  val title: Title,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val createdBy: Int,
  val tagCollection: TagCollection
) {

}
