package com.kujilabo.models.elasticsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class EsSearchHits {

  var total: Int = 0
  var hits: List[EsSearchHit] = List.empty

  def getHits(): List[EsSearchHit] = {
    this.hits
  }

  def getTotalHits(): Int = this.total
}
