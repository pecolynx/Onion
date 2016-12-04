package com.kujilabo.models.elasticsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class EsSearchResponse
(
) {

  val took: Int = 0
  val hits: EsSearchHits = null

  def getHits() = hits

}
