package models.elasticsearch.db

import config.Constants
import models.elasticsearch.IndexName
import org.joda.time.DateTime
import utils.JsonUtils
import com.kujilabo.models.core._
import com.kujilabo.common._

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
