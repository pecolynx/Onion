package models.elasticsearch.es

import com.kujilabo.models.core._
import models.elasticsearch.es.EsFieldType.EsFieldTypeString
import viewmodels.VmDocumentField

case class EsDocumentFieldString
(
  val fieldName: VariableName, val value: String
) extends EsDocumentField {

  def fieldType: EsFieldType = EsFieldTypeString

  def esValue: Any = value

  //def value: AnyRef = value

}
