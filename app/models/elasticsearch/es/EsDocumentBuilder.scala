package models.elasticsearch.es

import models.core.VariableName

object EsDocumentBuilder {

  def buildDocumentFieldList(sourceMap: Map[VariableName, Any],
                             esFieldList: EsFieldList): EsDocumentFieldList = {
    val esFieldMap: Map[VariableName, EsField] = esFieldList.map
    val userFieldSourceMap = sourceMap.filter(x => !EsSystemField.isSystemFieldName(x._1))

    return new EsDocumentFieldList(
      buildDefinedUserDocumentFieldList(userFieldSourceMap, esFieldMap) ++
        buildUndefinedUserDocumentFieldList(userFieldSourceMap, esFieldMap)
    )
  }

  def buildDefinedUserDocumentFieldList(userFieldSourceMap: Map[VariableName, Any],
                                        esFieldMap: Map[VariableName, EsField]): List[EsDocumentField] = {
    userFieldSourceMap.filter(x => x._2 != null).map { x =>
      buildDocumentField(esFieldMap.get(x._1).get, x._2)
    }.toList
  }

  def buildUndefinedUserDocumentFieldList(userFieldSourceMap: Map[VariableName, Any],
                                          esFieldMap: Map[VariableName, EsField]): List[EsDocumentField] = {
    userFieldSourceMap.filter(x => x._2 == null).map { x =>
      EsDocumentFieldEmpty(esFieldMap.get(x._1).get.name)
    }.toList
  }


  def buildDocumentField(esField: EsField, value: Any): EsDocumentField = {
    EsDocumentFieldFactory.build(esField.fieldType.name, esField.name, value)
  }
}
