package com.kujilabo.models.elasticsearch.es

import com.kujilabo.models.core.VariableName
import com.kujilabo.models.elasticsearch.MappingName
import org.scalatest._

class EsMappingTest extends FunSpec with ShouldMatchers {
  describe("map") {
    it("フィールドが空のとき") {
      val esMapping = new EsMapping(
        new MappingName(new VariableName("a")),
        new EsFieldList(List.empty[EsField]))
      val map = esMapping.map("a").asInstanceOf[Map[String, Object]]
      val properties = map("properties").asInstanceOf[Map[String, Object]]
      properties.size should equal(EsSystemField.ES_SYSTEM_FIELD_LIST.size + 0)
    }
  }
}
