package com.kujilabo.models.elasticsearch.es

/**
  * ElasticSearchアナライザクラス。
  *
  * @param name 名前
  */
abstract sealed class EsFieldAnalyzer(val name: String)

object EsFieldAnalyzer {

  case object EsFieldAnalyzerKuromoji extends EsFieldAnalyzer("kuromoji_analyzer")

  case object EsFieldAnalyzerNone extends EsFieldAnalyzer("")

  def parse(name: String): EsFieldAnalyzer = {
    name match {
      case "kuromoji_analyzer" => EsFieldAnalyzerKuromoji
      case "" => EsFieldAnalyzerNone
      case _ => throw new RuntimeException()
    }
  }
}

