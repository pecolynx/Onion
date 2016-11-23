package models.elasticsearch

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import utils.JsonUtils

@JsonIgnoreProperties(ignoreUnknown = true)
class EsSearchResponse
(
) {

  val took: Int = 0
  val hits: EsSearchHits = null

  def getHits() = hits

}
