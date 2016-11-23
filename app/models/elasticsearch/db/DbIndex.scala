package models.elasticsearch.db

import config.Constants
import models.core.{AppModelT, ModelIdImplT, ModelIdT}
import models.elasticsearch.IndexName
import org.joda.time.DateTime
import utils.JsonUtils

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
    this(Constants.DEFAULT_ID, Constants.DEFAULT_VERSION, new DateTime(), new DateTime(),
      name
    )
  }

  override def toString(): String = {
    JsonUtils.toJson(this)
  }
}
