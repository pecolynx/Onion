package models.elasticsearch.es

import javax.validation.constraints.Min

import config.Constants
import models.core.{BaseObject, ModelIdImplT, ModelIdT, Title}
import models.elasticsearch.{IndexName, MappingName}
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}
import viewmodels.VmDocument

import scala.annotation.meta.field

/**
  * ElasticSearchドキュメント。
  *
  * @param id                ドキュメントId
  * @param version           バージョン
  * @param createdAt         作成日時
  * @param updatedAt         更新日時
  * @param indexName         インデックス名
  * @param mappingName       マッピング名
  * @param title             タイトル
  * @param createdBy         作成者
  * @param documentFieldList ドキュメントフィールド一覧
  */
class EsDocument
(
  val id: ModelIdT[String],
  @(Min@field)(value = 1)
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val indexName: IndexName,
  val mappingName: MappingName,
  val title: Title,
  val createdBy: Int,
  val documentFieldList: EsDocumentFieldList
) extends BaseObject {

  this.validate()

  def this(indexName: IndexName,
           mappingName: MappingName,
           title: Title,
           createdBy: Int,
           documentFieldList: EsDocumentFieldList) = {
    this(
      Constants.DEFAULT_ID_STRING, Constants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      indexName, mappingName,
      title, createdBy, documentFieldList
    )
  }

  def map: Map[String, Any] = {
    val parserISO = ISODateTimeFormat.dateTimeNoMillis()
    var map: Map[String, Any] = this.documentFieldList.fieldNameValueMap
    map += (EsSystemField.TITLE.value -> this.title.value)
    map += (EsSystemField.CREATED_AT.value -> parserISO.print(this.createdAt.withZone(DateTimeZone.UTC)))
    map += (EsSystemField.UPDATED_AT.value -> this.updatedAt.toString())
    map += (EsSystemField.CREATED_BY.value -> this.createdBy)
    return map
  }
}
