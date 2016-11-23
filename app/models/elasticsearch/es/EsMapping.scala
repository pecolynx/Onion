package models.elasticsearch.es

import models.core.VariableName
import models.elasticsearch.MappingName
import models.elasticsearch.es.EsFieldAnalyzer.{EsFieldAnalyzerKuromoji, EsFieldAnalyzerNone}
import models.elasticsearch.es.EsFieldFormat.{EsFieldFormatDateOptionalTime, EsFieldFormatNone}
import models.elasticsearch.es.EsFieldType.{EsFieldTypeDate, EsFieldTypeInteger, EsFieldTypeString}

/**
  * ElasticSearchマッピング。
  *
  * @param name          マッピング名
  * @param userFieldList ユーザー定義フィールド一覧
  */
class EsMapping
(
  val name: MappingName,
  val userFieldList: EsFieldList
) {

  val id = Map("path" -> "data_id")
  val timestamp = Map("enabled" -> "true", "path" -> "updated_at")
  val all = Map("enabled" -> "true", "analyzer" -> "kuromoji_analyzer", "store" -> "true")
  val enabled = Map("enabled" -> "true")

  def isSystemField(field: EsField): Boolean = {
    EsSystemField.isSystemFieldName(field.name)
  }

  val fieldList = {
    val userList = userFieldList.list.filterNot(isSystemField)

    new EsFieldList(EsSystemField.ES_SYSTEM_FIELD_LIST ::: userList.toList)
  }

  private val _map = {
    var map2 = Map.empty[String, Object]
    map2 += ("_id" -> this.id)
    map2 += ("_timestamp" -> this.timestamp)
    map2 += ("properties" -> this.fieldList.getStringObjectMap)
    Map(this.name.value.value -> map2)
  }

  val map = _map
}
