package models.elasticsearch.es

import com.kujilabo.models.core._
import models.elasticsearch.es.EsFieldAnalyzer.{EsFieldAnalyzerKuromoji, EsFieldAnalyzerNone}
import models.elasticsearch.es.EsFieldFormat.{EsFieldFormatDateOptionalTime, EsFieldFormatNone}
import models.elasticsearch.es.EsFieldType.{EsFieldTypeDate, EsFieldTypeInteger, EsFieldTypeString}


import com.kujilabo.models.core._

object EsSystemField {

  val TITLE: VariableName = new VariableName("title")
  val DATA_ID: VariableName = new VariableName("data_id")
  val MANUAL_TAGS: VariableName = new VariableName("manual_tags")
  val AUTO_TAGS: VariableName = new VariableName("auto_tags")
  val SYSTEM_TAGS: VariableName = new VariableName("system_tags")
  val CREATED_AT: VariableName = new VariableName("created_at")
  val UPDATED_AT: VariableName = new VariableName("updated_at")
  val CREATED_BY: VariableName = new VariableName("created_by")

  val ID: VariableName = new VariableName("_id")
  val SOURCE: VariableName = new VariableName("_source")
  val ALL: VariableName = new VariableName("_all")
  val TIMESTAMP: VariableName = new VariableName("_timestamp")
  val ROUTING: VariableName = new VariableName("_routing")
  val PARENT: VariableName = new VariableName("_parent")

  def isSystemFieldName(name: VariableName): Boolean = {
    if (name.equals(DATA_ID) || name.equals(TITLE) ||
      name.equals(MANUAL_TAGS) || name.equals(SYSTEM_TAGS) ||
      name.equals(CREATED_AT) || name.equals(UPDATED_AT) ||
      name.equals(CREATED_BY) ||
      name.equals(ROUTING) || name.equals(PARENT)) {
      return true
    }

    return false
  }

  val ES_SYSTEM_FIELD_DATA_ID = new EsField(DATA_ID, true, true, true,
    EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone)
  val ES_SYSTEM_FIELD_TITLE = new EsField(TITLE, true, true, true,
    EsFieldTypeString, EsFieldFormatNone, EsFieldAnalyzerKuromoji)

  val ES_SYSTEM_FIELD_MANUAL_TAGS = new EsField(MANUAL_TAGS, true, false, true,
    EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone)
  val ES_SYSTEM_FIELD_SYSTEM_TAGS = new EsField(SYSTEM_TAGS, true, false, true,
    EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone)
  val ES_SYSTEM_FIELD_AUTO_TAGS = new EsField(AUTO_TAGS, true, false, true,
    EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone)

  val ES_SYSTEM_FIELD_CREATED_AT = new EsField(CREATED_AT, true, false, true,
    EsFieldTypeDate, EsFieldFormatDateOptionalTime, EsFieldAnalyzerNone)
  val ES_SYSTEM_FIELD_UPDATED_AT = new EsField(UPDATED_AT, true, false, true,
    EsFieldTypeDate, EsFieldFormatDateOptionalTime, EsFieldAnalyzerNone)

  val ES_SYSTEM_FIELD_CREATED_BY = new EsField(CREATED_BY, true, false, true,
    EsFieldTypeInteger, EsFieldFormatNone, EsFieldAnalyzerNone)

  val ES_SYSTEM_FIELD_LIST = List(
    ES_SYSTEM_FIELD_DATA_ID,
    ES_SYSTEM_FIELD_TITLE,
    ES_SYSTEM_FIELD_MANUAL_TAGS,
    ES_SYSTEM_FIELD_SYSTEM_TAGS,
    ES_SYSTEM_FIELD_AUTO_TAGS,
    ES_SYSTEM_FIELD_CREATED_AT,
    ES_SYSTEM_FIELD_UPDATED_AT,
    ES_SYSTEM_FIELD_CREATED_BY
  )

}
