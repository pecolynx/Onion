package com.kujilabo.models.elasticsearch.db

import com.kujilabo.common._
import com.kujilabo.config.CommonConstants
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.IndexName
import com.kujilabo.util.JsonUtils
import org.joda.time.DateTime

class DbIndex
(
  val id: ModelIdT[Int],
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val name: IndexName
) extends AppModelT[Int](id, version, createdAt, updatedAt) {

  this.validate

  def this(name: IndexName) = {
    this(CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      name
    )
  }

  override def toString(): String = {
    JsonUtils.toJson(this)
  }
}
