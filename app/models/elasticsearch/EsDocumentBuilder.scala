package models.elasticsearch

import com.typesafe.scalalogging.LazyLogging
import config.Constants
import models.core._
import com.kujilabo.models.core._
import models.elasticsearch.es.{EsDocumentFieldString, _}
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.highlight.HighlightField
import org.elasticsearch.search.{SearchHit, SearchHitField}
import org.joda.time.DateTime

import scala.collection.JavaConversions._

class EsDocumentBuilder extends LazyLogging {

  def buildHitList(response: EsSearchResponse,
                   getEsMapping: (IndexName, MappingName) => EsMapping): List[Hit] = {
    response.getHits.getHits.map(this.buildHit(_, getEsMapping)).toList
  }

  def createDateTimeFromSearchHitField(searchHitField: EsSearchHitField): DateTime = {
    new DateTime(searchHitField.getValue[Object])
  }

  def getSearchHitField(searchHit: EsSearchHit, name: String): EsSearchHitField = {
    //println("searchHit : " + searchHit)
    searchHit.field(name)
  }

  def buildHit(searchHit: EsSearchHit, getEsMapping: (IndexName, MappingName) => EsMapping): Hit = {
    def getDateTime(name: VariableName): DateTime = {
      this.createDateTimeFromSearchHitField(this.getSearchHitField(searchHit, name.value))
    }


    val indexName = new IndexName(new VariableName(searchHit.getIndex))
    val mappingName = new MappingName(new VariableName(searchHit.getType))

    val esMapping = getEsMapping(indexName, mappingName)
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
      new ModelIdImplT[String](searchHit.getId), searchHit.getVersion.toInt,
      createdAt, updatedAt, indexName, mappingName,
      title, createdBy, esDocumentFieldList)

    new Hit(document, title.value, searchHit.getScore)
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
                        highlightList: Map[String, EsHighlightField]): String = {
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

  def createDocumentFieldList
  (
    esFieldList: EsFieldList,
    hitFieldList: Map[String, EsSearchHitField],
    highlightList: Map[String, EsHighlightField]
  ): EsDocumentFieldList = {
    var esDocumentFieldList = List.empty[EsDocumentField]

    val esFieldMap = esFieldList.map

    esFieldList.list.
      filterNot((x) => EsSystemField.isSystemFieldName(x.name)).
      foreach { x =>
        if (!esFieldMap.contains(x.name)) {
          throw new RuntimeException()
        }

        hitFieldList.get(x.name.value) match {
          case None =>
            logger.warn("not match name = " + x.name.value)
            esDocumentFieldList :+= this.getUndefinedDocumentField(highlightList, x.name)
          case y: Some[EsSearchHitField] =>
            val searchHitField = y.get
            val value = this.getHighlightValue(x.name.value,
              searchHitField.getValue.asInstanceOf[String], highlightList)

            logger.warn("highlight value = " + value)
            esDocumentFieldList :+= new EsDocumentFieldString(x.name, value)
        }
      }

    return new EsDocumentFieldList(esDocumentFieldList)
  }

  def getUndefinedDocumentField(highlightList: Map[String, EsHighlightField],
                                name: VariableName): EsDocumentFieldString = {
    val value = this.getHighlightValue(name.value, "", highlightList)
    new EsDocumentFieldString(name, value)
  }

  def getDefinedDocumentField
  (
    highlightList: Map[String, EsHighlightField],
    searchHitField: EsSearchHitField,
    name: VariableName
  ): EsDocumentFieldString = {
    val value = this.getHighlightValue(name.value,
      searchHitField.getValue.asInstanceOf[String], highlightList)
    new EsDocumentFieldString(name, value)
  }
}
