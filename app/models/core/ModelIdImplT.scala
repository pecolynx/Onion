package models.core

/**
  * モデルId。
  *
  * @param value モデルId
  * @tparam T モデルIdの型
  */
class ModelIdImplT[T](val value: T) extends ModelIdT[T] {

  override def hashCode = this.toString().hashCode

  override def toString() = this.value.toString()

  override def equals(other: Any) = {
    other match {
      case that: ModelIdImplT[T] =>
        that.canEqual(this) && that.toString() == this.toString()
      case _ => false
    }
  }

  def canEqual(other: Any) = other.isInstanceOf[ModelIdImplT[T]]
}
