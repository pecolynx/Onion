package com.kujilabo.models.elasticsearch

import com.kujilabo.models.elasticsearch.es.{EsField, EsFieldList}

/**
  * フィールド一覧。
  *
  * @param list フィールド一覧
  */
class FieldList(val list: Seq[Field]) {
  def toEsFieldList(): EsFieldList = new EsFieldList(this.list.map(this.toEsField))

  def toEsField(field: Field): EsField = {
    new EsField(field.esName, field.esStore, field.esIncludeInAll, field.esRequired,
      field.esFieldType, field.esFieldFormat, field.esFieldAnalyzer)
  }
}
