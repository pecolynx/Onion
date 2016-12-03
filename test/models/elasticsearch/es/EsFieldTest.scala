package models.elasticsearch.es

import com.kujilabo.models.core.VariableName
import models.elasticsearch.es.EsFieldAnalyzer.EsFieldAnalyzerNone
import models.elasticsearch.es.EsFieldFormat.EsFieldFormatNone
import models.elasticsearch.es.EsFieldType.EsFieldTypeInteger
import org.scalatest._

class EsFieldTest extends FunSpec with ShouldMatchers {
  describe("a") {
    it("b") {
      val field = new EsField(new VariableName("a"), true, true, true, EsFieldTypeInteger,
        EsFieldFormatNone, EsFieldAnalyzerNone)
      println(field.map.toString)
    }
  }
}
