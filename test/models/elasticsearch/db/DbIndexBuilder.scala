package models.elasticsearch.db

import com.kujilabo.models.core.VariableName
import models.elasticsearch.IndexName

class DbIndexBuilder {
  var name: IndexName = new IndexName(new VariableName("name"))

  def build(): DbIndex = {
    new DbIndex(name)
  }

  def withName(name: String): DbIndexBuilder = {
    this.name = new IndexName(new VariableName(name))
    this
  }

  def withName(name: IndexName): DbIndexBuilder = {
    this.name = name
    this
  }
}
