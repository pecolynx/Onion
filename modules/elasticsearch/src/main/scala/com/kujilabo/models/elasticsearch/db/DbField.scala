package com.kujilabo.models.elasticsearch.db

import javax.validation.constraints.Size

import com.kujilabo.common._
import com.kujilabo.config.CommonConstants
import com.kujilabo.models.core._
import com.kujilabo.util.JsonUtils
import org.joda.time.DateTime

import scala.annotation.meta.field

class DbField
(
  val id: ModelIdT[Int],
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val name: VariableName,
  val fieldType: Int,
  val seq: Int,
  val mappingId: ModelIdT[Int],
  @(Size@field)(min = 1, max = 20)
  val multiLangName: String
) extends AppModelT[Int](
  id,
  version,
  createdAt,
  updatedAt
) {
  this.validate

  def this
  (
    name: VariableName, fieldType: Int, seq: Int, mappingId: ModelIdT[Int],
    multiLangName: String) = {
    this(
      CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      name,
      fieldType,
      seq,
      mappingId,
      multiLangName
    )
  }

  override def toString(): String = {
    JsonUtils.toJson(this)
  }
}
