package models.elasticsearch

class EsSearchHitField
(
  val name: String,
  val value: Any
) {

  def this() = {
    this("", null)
  }

  def getValue[T](): T = {
    val list = value.asInstanceOf[List[T]]
    list(0)
  }

}
