package models.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import config.Constants
import models.core.{ModelIdImplT, Title, VariableName}
import models.elasticsearch.es.{EsDocumentFieldEmpty, EsDocumentFieldString, _}
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.{SearchHit, SearchHitField}
import org.elasticsearch.search.highlight.HighlightField
import org.joda.time.DateTime
import service.elasticsearch.es.EsMappingService
import utils.StringUtils

import scala.collection.JavaConversions._
import collection.JavaConversions._

class EsDocumentBuilderOld extends LazyLogging {

  def buildHitList(response: SearchResponse, getEsMappingFunction: Function2[IndexName, MappingName, EsMapping]): List[Hit] = {
    response.getHits.getHits.map(this.buildHit(_, getEsMappingFunction)).toList
  }

  def createDateTimeFromSearchHitField(searchHitField: SearchHitField): DateTime = {
    new DateTime(searchHitField.getValue[Object])
  }

  def getSearchHitField(searchHit: SearchHit, name: String): SearchHitField = {
    searchHit.field(name)
  }

  def buildHit(searchHit: SearchHit, getEsMappingFunction: Function2[IndexName, MappingName, EsMapping]): Hit = {

    //logger.warn(searchHit.toString)
    def getDateTime(name: VariableName): DateTime = {
      this.createDateTimeFromSearchHitField(this.getSearchHitField(searchHit, name.value))
    }


    val indexName = new IndexName(new VariableName(searchHit.getIndex))
    val mappingName = new MappingName(new VariableName(searchHit.getType))

    val esMapping = getEsMappingFunction(indexName, mappingName)
    val hitFieldList = searchHit.getFields()
    val highlightList = searchHit.getHighlightFields()
    val esDocumentFieldList = this.createDocumentFieldList(
      esMapping.fieldList, hitFieldList, highlightList)

    val createdAt = getDateTime(EsSystemField.CREATED_AT)
    val updatedAt = getDateTime(EsSystemField.UPDATED_AT)
    val title = new Title(this.getHighlightValue(EsSystemField.TITLE.value,
      searchHit.field(EsSystemField.TITLE.value).getValue[String], highlightList))
    val createdBy = searchHit.field(EsSystemField.CREATED_BY.value).getValue[Int]

    val document = new EsDocument(
      new ModelIdImplT[String](searchHit.getId), searchHit.getVersion().toInt,
      createdAt, updatedAt, indexName, mappingName,
      title, createdBy, esDocumentFieldList)

    new Hit(document, title.value, searchHit.getScore())
  }

  /**
    * ハイライトしたフィールドの値を取得します。
    *
    * @param key           フィールド名
    * @param value         フィールドの値
    * @param highlightList ハイライト一覧
    * @return ハイライトしたフィールドの値
    */
  def getHighlightValue(key: String, value: String,
                        highlightList: java.util.Map[String, HighlightField]): String = {
    if (highlightList.contains(key)) {
      highlightList(key).fragments.toList.head.string
    }
    else {
      if (value.length() > Constants.HIT_RESULT_CONTENT_MAX_LENGTH) {
        value.substring(0, Constants.HIT_RESULT_CONTENT_MAX_LENGTH)
      }
      else {
        value
      }
    }
  }

  def createDocumentFieldList(esFieldList: EsFieldList,
                              hitFieldList: java.util.Map[String, SearchHitField],
                              highlightList: java.util.Map[String, HighlightField]):
  EsDocumentFieldList = {
    var esDocumentFieldList = List.empty[EsDocumentField]

    val esFieldMap = esFieldList.map

    esFieldList.list.foreach { x =>
      if (!EsSystemField.isSystemFieldName(x.name)) {
        if (!esFieldMap.contains(x.name)) {
          throw new RuntimeException()
        }

        val searchHitField: SearchHitField = hitFieldList.get(x.name.value)
        if (searchHitField == null) {
          val value = this.getHighlightValue(x.name.value, "", highlightList)

          //logger.warn("value = " + value)
          esDocumentFieldList :+= new EsDocumentFieldString(x.name, value)
        }
        else {
          val value = this.getHighlightValue(x.name.value,
            searchHitField.getValue.asInstanceOf[String], highlightList)

          //logger.warn("value = " + value)
          esDocumentFieldList :+= new EsDocumentFieldString(x.name, value)
        }
      }
    }

    return new EsDocumentFieldList(esDocumentFieldList)
  }

  def getHighlightList(searchHit: SearchHit): Map[String, HighlightField] = {
    var title = ""
    var value = ""
    val map: scala.collection.mutable.Map[String, HighlightField] = searchHit.getHighlightFields
    return map.toMap
  }
}
