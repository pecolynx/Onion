package utils

import com.typesafe.scalalogging.LazyLogging
import models._
import models.core._
import com.kujilabo.models.core._
import models.elasticsearch._
import models.elasticsearch.es.EsSystemField
import org.joda.time.DateTime

object ResponseUtil extends LazyLogging {

  def createResponseOfDocumentAddFromJson(json: String): Option[ResponseOfDocumentAdd] = {
    val map = JsonUtils.toMap(json)

    val index = (map.get("_index").asInstanceOf[Some[String]])
    val mapping = (map.get("_type").asInstanceOf[Some[String]])
    val id = (map.get("_id").asInstanceOf[Some[String]])
    val version = getVersion(map)

    createResponseOfDocumentAdd(index, mapping, id, version)
  }

  def createResponseOfDocumentAdd
  (
    index: Option[String], mapping: Option[String], id: Option[String],
    version: Option[Int]
  ): Option[ResponseOfDocumentAdd] = {
    for {
      _index <- index
      _mapping <- mapping
      _id <- id
      _version <- version
    } yield {
      new ResponseOfDocumentAdd(new IndexName(new VariableName(_index)),
        new MappingName(new VariableName(_mapping)),
        new ModelIdImplT[String](_id), _version)
    }
  }

  def getSource(map: Map[String, Any]): Option[Map[VariableName, Any]] = {
    for {
      obj <- map.get("_source")
    } yield {
      val sourceMap = obj.asInstanceOf[Map[String, Any]]
      var sourceMap2 = Map.empty[VariableName, Any]
      sourceMap.foreach { x =>
        sourceMap2 += (new VariableName(x._1) -> x._2)
      }
      return Some(sourceMap2)
    }
  }

  def createSystemField(map: Map[VariableName, Any]): Option[SystemDocumentFieldList] = {
    val title = (map.get(EsSystemField.TITLE).asInstanceOf[Some[String]])
    val createdAt = (map.get(EsSystemField.CREATED_AT).asInstanceOf[Some[String]])
    val updatedAt = (map.get(EsSystemField.UPDATED_AT).asInstanceOf[Some[String]])
    val createdBy = (map.get(EsSystemField.CREATED_BY).asInstanceOf[Some[Int]])

    createSystemField(title, createdAt, updatedAt, createdBy)
  }

  def createSystemField
  (title: Option[String], createdAt: Option[String],
   updatedAt: Option[String], createdBy: Option[Int]): Option[SystemDocumentFieldList] = {
    for {
      _title <- title
      _createdAt <- this.toDateTime(createdAt)
      _updatedAt <- this.toDateTime(updatedAt)
      _createdBy <- createdBy
    } yield {
      new SystemDocumentFieldList(new Title(_title), _createdAt, _updatedAt, _createdBy,
        new TagCollection(new TagList(List.empty[Tag]), new TagList(List.empty[Tag])))
    }
  }

  def toDateTime(value: Option[String]): Option[DateTime] = {
    value.flatMap((a: String) => Some(new DateTime(a)))
  }

  def getVersion(map: Map[String, Any]): Option[Int] = {
    map.get("_version").asInstanceOf[Some[Int]]
  }


}
