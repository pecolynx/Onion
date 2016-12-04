package com.kujilabo.models.elasticsearch.es

class EsDocumentFieldList
(
  val list: Seq[EsDocumentField]
) {

  val fieldNameValueMap = toFieldNameValueMap


  private def toFieldNameValueMap(): Map[String, Any] = {
    this.list.map { x =>
      x.fieldName.value -> x.esValue
    }.toMap
  }

  def getString(name: String): String = {
    this.fieldNameValueMap.get(name) match {
      case None => throw new Exception("field not found.")
      case Some(x) => x.asInstanceOf[String]
    }
  }

}
