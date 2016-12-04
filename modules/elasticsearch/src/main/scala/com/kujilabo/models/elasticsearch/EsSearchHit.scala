package com.kujilabo.models.elasticsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kujilabo.common.ModelNotFoundException
import com.kujilabo.models.elasticsearch.es.{EsHighlightField, EsText}

@JsonIgnoreProperties(ignoreUnknown = true)
class EsSearchHit {
  val _index: String = ""

  val _type: String = ""
  val _id: String = ""
  val _version: Int = 0
  val _score: Double = 0

  val fields: Map[String, Any] = Map.empty

  val highlight: Map[String, List[String]] = Map.empty

  def getId: String = this._id

  def getVersion: Int = this._version

  def getScore: Double = this._score

  def getIndex: String = this._index


  def getType: String = this._type

  def getFields(): Map[String, EsSearchHitField] = {
    this.fields.map { case (k, v) => (k -> new EsSearchHitField(k, v)) }
  }

  def getHighlightFields(): Map[String, EsHighlightField] = {
    val fields = this.highlight.map {
      case (k, v) => (k -> new EsHighlightField(
        k, v.map(x => new EsText(x))
      ))
    }
    return fields
  }

  def field(name: String): EsSearchHitField = {
    this.getFields.get(name) match {
      case None => throw new ModelNotFoundException(name)
      case x: Some[EsSearchHitField] => x.get
    }
  }

  override def toString(): String = {
    "index : " + this._index + ",getFields : " + this.getFields()

  }
}
