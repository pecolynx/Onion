package com.kujilabo.models.elasticsearch.es

import com.kujilabo.models.core.VariableName

/**
  * ElasticSearchフィールド。
  *
  * @param name          フィールド名
  * @param store         Store
  * @param includeInAll  IncludeInAll
  * @param required      Required
  * @param fieldType     Type
  * @param fieldFormat   Format
  * @param fieldAnalyzer Analyzer
  */
class EsField
(
  val name: VariableName,
  val store: Boolean,
  val includeInAll: Boolean,
  val required: Boolean,
  val fieldType: EsFieldType,
  val fieldFormat: EsFieldFormat,
  val fieldAnalyzer: EsFieldAnalyzer
) {

  override def toString(): String = {
    this.name.toString()
  }

  private val _map = {
    var map2 = collection.mutable.Map.empty[String, AnyRef]
    map2 += ("type" -> this.fieldType.name)
    map2 += ("store" -> this.store.toString())
    if (!this.fieldFormat.name.isEmpty) {
      map2 += ("format" -> this.fieldFormat.name)
    }

    if (!this.fieldAnalyzer.name.isEmpty()) {
      map2 += ("analyzer" -> this.fieldAnalyzer.name)
      map2 += ("mode" -> "search")
      //map2 += ("mode" -> "normal")
    }

    map2 += ("include_in_all" -> this.includeInAll.toString())

    Map(this.name.value -> map2)
  }

  val map: Map[String, AnyRef] = _map
}
