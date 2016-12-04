package com.kujilabo.models.elasticsearch

import com.kujilabo.common._
import com.kujilabo.config.CommonConstants
import com.kujilabo.models.core._
import org.joda.time.DateTime

class Tag
(
  val id: ModelIdT[Int],
  val version: Int,
  val createdAt: DateTime,
  val updatedAt: DateTime,
  val name: VariableName,
  val multiLangName: String
) extends AppModelT[Int](id, version, createdAt, updatedAt) {
  this.validate

  def this(name: VariableName, multiLangName: String) = {
    this(
      CommonConstants.DEFAULT_ID, CommonConstants.DEFAULT_VERSION,
      new DateTime(), new DateTime(),
      name,
      multiLangName
    )
  }
}
