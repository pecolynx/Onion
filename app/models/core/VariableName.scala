package models.core

/**
  * 変数名。
  *
  * @param value 名前
  */
class VariableName(val value: String) {

  override def hashCode = this.toString().hashCode

  override def toString(): String = this.value

  override def equals(other: Any) = {
    other match {
      case that: VariableName => that.canEqual(this) && that.toString() == this.toString()
      case _ => false
    }
  }

  def canEqual(other: Any) = other.isInstanceOf[VariableName]
}
