package models.core

import javax.validation.constraints.Size

import config.Constants
import org.hibernate.validator.constraints.NotEmpty

import scala.annotation.meta.field
import com.kujilabo.models.core._
import com.kujilabo.common._
import com.kujilabo.config.CommonConstants

/**
  * 言語。
  *
  * @param id   モデルId
  * @param code 言語コード(ja, en, etc...)
  * @param name 名前
  */
class Lang
(
  val id: ModelIdT[Int],
  @(Size@field)(min = 2, max = 2)
  val code: String,
  @(NotEmpty@field)
  val name: String
) extends BaseObject {

  this.validate()

  /**
    * コンストラクタ。
    *
    * @param code 言語コード(ja, en, etc...)
    * @param name 名前
    */
  def this(code: String, name: String) = {
    this(
      CommonConstants.DEFAULT_ID,
      code,
      name
    )
  }

  override def hashCode = this.toString().hashCode

  override def toString(): String = this.code.toString()

  override def equals(other: Any) = {
    other match {
      case that: Lang => that.canEqual(this) && that.toString() == this.toString()
      case _ => false
    }
  }

  def canEqual(other: Any) = other.isInstanceOf[Lang]
}
