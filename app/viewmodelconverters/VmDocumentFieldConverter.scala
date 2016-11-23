package viewmodelconverters

import models.elasticsearch.es.{EsDocumentField, EsDocumentFieldEmpty, EsDocumentFieldString}
import viewmodels.VmDocumentField

object VmDocumentFieldConverter {

  def toViewModel(model: EsDocumentField): VmDocumentField = {
    model match {
      case x: EsDocumentFieldString => new VmDocumentField(x.fieldName.value, List(x.value), 1, "")
      case x: EsDocumentFieldEmpty => new VmDocumentField(x.fieldName.value, List(""), 1, "")
      case _ => {
        println(model.getClass)
        println("VmDocumentFieldConverter.toViewModel model : " + model)
        throw new RuntimeException
      }
    }
  }
}
