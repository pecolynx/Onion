package models.elasticsearch.es

/**
  * ElasticSearchフォーマット。
  *
  * @param name
  */
sealed abstract class EsFieldFormat(val name: String)


object EsFieldFormat {

  case object EsFieldFormatDateOptionalTime extends EsFieldFormat("dateOptionalTime")

  case object EsFieldFormatNone extends EsFieldFormat("")

  def parse(name: String): EsFieldFormat = {
    name match {
      case "dateOptionalTime" => EsFieldFormatDateOptionalTime
      case "" => EsFieldFormatNone
      case _ => throw new RuntimeException()
    }
  }
}
