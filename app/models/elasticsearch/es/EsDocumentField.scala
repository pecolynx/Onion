package models.elasticsearch.es

import models.core.VariableName
import models.elasticsearch.es.EsFieldType.{EsFieldTypeDate, EsFieldTypeInteger, EsFieldTypeString}
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import utils.JsonUtils
import viewmodels.VmDocumentField

trait EsDocumentField {
  def fieldName: VariableName

  def value: Any

  def esValue: Any

  override def toString(): String = JsonUtils.toJson(this)
}

case class EsDocumentFieldInteger
(
  val fieldName: VariableName, val value: Int
) extends EsDocumentField {

  def fieldType: EsFieldType = EsFieldTypeInteger

  def esValue: Any = value

  //def value: AnyRef = value

}

case class EsDocumentFieldDateTime
(
  val fieldName: VariableName, val value: DateTime
) extends EsDocumentField {

  def fieldType: EsFieldType = EsFieldTypeDate

  def esValue: Any = {
    return this.value.toString()
    //val parserISO = ISODateTimeFormat.dateTimeNoMillis()
    //parserISO.print(this.value.withZone(DateTimeZone.UTC))
  }

  //def value: AnyRef = value

}
