package models.elasticsearch.db

import com.kujilabo.models.core.{ModelIdImplT, ModelIdT, VariableName}
import models.elasticsearch.MappingName

class DbFieldBuilder {
  var name: VariableName = new VariableName("name")
  var fieldType: Int = 1
  var seq: Int = 1
  var mappingId: ModelIdT[Int] = new ModelIdImplT[Int](1)
  var multiLangName: String = "名前"

  def build(): DbField = {
    new DbField(name, fieldType, seq, mappingId, multiLangName)
  }

  def withMappingId(mappingId: ModelIdT[Int]): DbFieldBuilder = {
    this.mappingId = mappingId
    this
  }

  def withName(name: String): DbFieldBuilder = {
    this.name = new VariableName(name)
    this
  }

  def withName(name: VariableName): DbFieldBuilder = {
    this.name = name
    this
  }

  def withSeq(seq: Int): DbFieldBuilder = {
    this.seq = seq
    this
  }

  def withMultiLangName(multiLangName: String): DbFieldBuilder = {
    this.multiLangName = multiLangName
    this
  }
}