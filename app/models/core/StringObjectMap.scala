package models.core

class StringObjectMap(val map: Map[String, AnyRef]) {
  override def toString() = this.map.toString()

}
