package com.kujilabo.models.elasticsearch.es

import com.kujilabo.common.ModelNotFoundException
import com.kujilabo.models.core._
import com.kujilabo.util.JsonUtils
import com.typesafe.scalalogging.LazyLogging

class EsFieldList(val list: Seq[EsField]) extends LazyLogging {

  def map: Map[VariableName, EsField] = {
    var map = Map.empty[VariableName, EsField]
    this.list.foreach { x =>
      map += (x.name -> x)
    }

    return map
  }

  def getStringObjectMap: Map[String, Object] = {
    var map = Map.empty[String, Object]
    this.list.foreach { x =>
      map = map ++ x.map
    }

    map
  }


  def get(name: VariableName): EsField = {
    this.list.foreach { x => logger.warn(x.toString()) }

    val found = this.list.filter(x => x.name.equals(name))
    if (found.isEmpty) {
      throw new ModelNotFoundException(name.toString())
    }

    return found.head
  }

  override def toString(): String = {
    return JsonUtils.toJson(this)
  }
}
