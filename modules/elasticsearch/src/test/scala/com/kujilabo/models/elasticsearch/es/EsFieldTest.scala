package com.kujilabo.models.elasticsearch.es

import com.kujilabo.models.core.VariableName
import com.kujilabo.models.elasticsearch.es.EsFieldAnalyzer.EsFieldAnalyzerNone
import com.kujilabo.models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import com.kujilabo.models.elasticsearch.es.EsFieldType.EsFieldTypeInteger
import org.scalatest._

class EsFieldTest extends FunSpec with ShouldMatchers {
  describe("map") {
    describe("storeがtrueのとき") {
      it("storeにtrueが設定されること") {
        val field = new EsField(new VariableName("a"), true, true, true, EsFieldTypeInteger,
          EsFieldFormatNone, EsFieldAnalyzerNone)
        val map = field.map("a").asInstanceOf[collection.mutable.Map[String, AnyRef]]
        map("store") should equal("true")
      }
    }
  }
}
