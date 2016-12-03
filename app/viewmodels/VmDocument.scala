package viewmodels

import javax.validation.constraints.Min

import models.core._
import com.kujilabo.models.core._
import models.elasticsearch.es.EsDocumentFieldList
import models.elasticsearch.{IndexName, MappingName}
import org.joda.time.DateTime

import scala.annotation.meta.field

class VmDocument
(
  val id: String,
  @(Min@field)(value = 1)
  val version: Int,
  val createdAt: String,
  val updatedAt: String,
  val indexName: String,
  val mappingName: String,
  val title: String,
  val documentFieldList: List[VmDocumentField]
) extends BaseObject {
  this.validate()


}
