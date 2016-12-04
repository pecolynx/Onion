package com.kujilabo.models.elasticsearch.es

import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.es.EsFieldType.{EsFieldTypeDate, EsFieldTypeInteger}
import com.kujilabo.util.JsonUtils
import org.joda.time.DateTime

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
