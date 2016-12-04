package com.kujilabo.models.elasticsearch

import com.kujilabo.models.core.{BaseObject, VariableName}
import com.kujilabo.validation.VariableNameSize

import scala.annotation.meta.field

/**
  * マッピング名。
  *
  * @param value 名前
  */
class MappingName
(
  @(VariableNameSize@field)(min = 1, max = 20)
  val value: VariableName
) extends BaseObject {

  this.validate

  override def hashCode = this.toString().hashCode

  override def toString(): String = this.value.toString()

  override def equals(other: Any) = {
    other match {
      case that: MappingName => that.canEqual(this) && that.toString() == this.toString()
      case _ => false
    }
  }

  def canEqual(other: Any) = other.isInstanceOf[MappingName]
}
