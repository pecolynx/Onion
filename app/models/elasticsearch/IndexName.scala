package models.elasticsearch

import models.core.{BaseObject, VariableName}
import validators.VariableNameSize

import scala.annotation.meta.field

/**
  * インデックス名クラス。
  *
  * @param value 名前
  */
class IndexName
(
  @(VariableNameSize@field)(min = 1, max = 20)
  val value: VariableName
) extends BaseObject {

  this.validate

  override def hashCode = this.toString().hashCode

  override def toString(): String = this.value.toString()

  override def equals(other: Any) = {
    other match {
      case that: IndexName => that.canEqual(this) && that.toString() == this.toString()
      case _ => false
    }
  }

  def canEqual(other: Any) = other.isInstanceOf[IndexName]
}
