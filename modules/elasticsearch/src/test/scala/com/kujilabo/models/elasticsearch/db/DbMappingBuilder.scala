package com.kujilabo.models.elasticsearch.db

import com.kujilabo.models.core.{ModelIdImplT, ModelIdT, VariableName}
import com.kujilabo.models.elasticsearch.MappingName

class DbMappingBuilder {
  var indexId: ModelIdT[Int] = new ModelIdImplT[Int](1)
  var name: MappingName = new MappingName(new VariableName("name"))

  def build(): DbMapping = {
    new DbMapping(indexId, name)
  }

  def withIndexId(indexId: ModelIdT[Int]): DbMappingBuilder = {
    this.indexId = indexId
    this
  }

  def withName(name: String): DbMappingBuilder = {
    this.name = new MappingName(new VariableName(name))
    this
  }

  def withName(name: MappingName): DbMappingBuilder = {
    this.name = name
    this
  }
}
