package models.elasticsearch.es

import com.kujilabo.models.core._
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import viewmodels.VmDocumentField

case class EsDocumentFieldEmpty(fieldName: VariableName) extends EsDocumentField {
  def fieldType: EsFieldType = EsFieldTypeString

  def value: Any = ""

  def esValue: Any = value
}
