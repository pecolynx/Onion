package models.elasticsearch.es

import models.core.VariableName
import models.elasticsearch.MappingName
import org.scalatest._

class EsMappingTest extends FunSpec with ShouldMatchers {
  describe("a") {
    it("b") {
      val esMapping = new EsMapping(
        new MappingName(new VariableName("a")),
        new EsFieldList(List.empty[EsField]))
      println(esMapping.map.toString())
      //println(esMapping.list.toString())
    }
  }
}
