package com.kujilabo.models.elasticsearch.db

import com.kujilabo.common._
import com.kujilabo.config.CommonConstants
import com.kujilabo.models.core._
import com.kujilabo.models.elasticsearch.MappingName
import com.kujilabo.util.JsonUtils
import org.joda.time.DateTime

class DbMapping
(
  val id: ModelIdT[Int],
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val indexId: ModelIdT[Int],
  val name: MappingName
) extends AppModelT[Int](id, version, createdAt, updatedAt) {

  this.validate

  def this(indexId: ModelIdT[Int], name: MappingName) = {
    this(
      CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      indexId,
      name
    )
  }

  override def toString(): String = {
    JsonUtils.toJson(this)
  }
}

