package models.elasticsearch.es

class EsHighlightField
(
  val name: String,
  val value: List[EsText]
) {

  def this() = {
    this("", List.empty)
  }

  def fragments: List[EsText] = this.value

}
