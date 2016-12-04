package com.kujilabo.models.elasticsearch.es

import com.kujilabo.models.core._

object EsDocumentFieldFactory {
  def build(fieldType: String, fieldName: VariableName, any: Any): EsDocumentField = {
    return new EsDocumentFieldString(fieldName, any.toString())
  }
}
