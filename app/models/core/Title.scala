package models.core

/**
  * タイトル。
  *
  * @param value タイトル
  */
class Title(val value: String) extends BaseObject {

  override def hashCode = this.toString().hashCode

  /**
    * 文字列表現を取得します。
    *
    * @return 文字列表現
    */
  override def toString(): String = this.value.toString()

  override def equals(other: Any) = {
    other match {
      case that: Title => that.canEqual(this) && that.toString() == this.toString()
      case _ => false
    }
  }

  def canEqual(other: Any) = other.isInstanceOf[Title]
}
