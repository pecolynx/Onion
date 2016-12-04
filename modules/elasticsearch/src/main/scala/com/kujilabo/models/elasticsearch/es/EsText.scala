package com.kujilabo.models.elasticsearch.es

class EsText
(
  value: String
) {
  override def toString: String = this.value

  def string: String = this.value

}
