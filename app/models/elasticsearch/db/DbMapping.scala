package models.elasticsearch.db

import config.Constants
import com.kujilabo.models.core._
import models.elasticsearch.MappingName
import org.joda.time.DateTime
import utils.JsonUtils
import com.kujilabo.common._

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

