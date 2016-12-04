package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core.VariableName
import com.kujilabo.models.elasticsearch.db.DbFieldType
import com.kujilabo.models.elasticsearch.es.{EsFieldAnalyzer, EsFieldFormat, EsFieldType}

/**
  * フィールド。
  *
  * @param esName          フィールド名
  * @param dbMultiLangName フィールド名(多言語)
  * @param esStore         Store
  * @param esIncludeInAll  IncludeInAll
  * @param esRequired      Required
  * @param esFieldType     Type
  * @param esFieldFormat   Format
  * @param esFieldAnalyzer Analyzer
  * @param dbFieldType     DbType
  * @param dbSeq           DbSeq
  */
class Field
(
  val esName: VariableName,
  val dbMultiLangName: String,
  val esStore: Boolean,
  val esIncludeInAll: Boolean,
  val esRequired: Boolean,
  val esFieldType: EsFieldType,
  val esFieldFormat: EsFieldFormat,
  val esFieldAnalyzer: EsFieldAnalyzer,
  val dbFieldType: DbFieldType,
  val dbSeq: Int
) {

}
