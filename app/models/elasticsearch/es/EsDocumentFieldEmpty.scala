package models.elasticsearch.es

import models.core.VariableName
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import viewmodels.VmDocumentField

case class EsDocumentFieldEmpty(fieldName: VariableName) extends EsDocumentField {
  def fieldType: EsFieldType = EsFieldTypeString

  def value: Any = ""

  def esValue: Any = value
}
