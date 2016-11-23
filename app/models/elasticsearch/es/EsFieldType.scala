package models.elasticsearch.es

sealed abstract class EsFieldType(val name: String)

object EsFieldType {

  case object EsFieldTypeInteger extends EsFieldType("integer")

  case object EsFieldTypeLong extends EsFieldType("long")

  case object EsFieldTypeString extends EsFieldType("string")

  case object EsFieldTypeDate extends EsFieldType("date")

  def parse(name: String): EsFieldType = {
    name match {
      case "integer" => EsFieldTypeInteger
      case "long" => EsFieldTypeLong
      case "string" => EsFieldTypeString
      case "date" => EsFieldTypeDate
      case _ => throw new RuntimeException(name)
    }
  }
}

