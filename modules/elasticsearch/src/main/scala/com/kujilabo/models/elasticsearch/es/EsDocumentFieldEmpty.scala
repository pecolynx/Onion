package com.kujilabo.models.elasticsearch.es

import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.es.EsFieldType.EsFieldTypeString

case class EsDocumentFieldEmpty(fieldName: VariableName) extends EsDocumentField {
  def fieldType: EsFieldType = EsFieldTypeString

  def value: Any = ""

  def esValue: Any = value
}
