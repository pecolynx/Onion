package models.elasticsearch.es

import models.core.VariableName

object EsDocumentFieldFactory {
  def build(fieldType: String, fieldName: VariableName, any: Any): EsDocumentField = {
    return new EsDocumentFieldString(fieldName, any.toString())
  }
}
