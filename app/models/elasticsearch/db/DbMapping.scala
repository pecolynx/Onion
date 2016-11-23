package models.elasticsearch.db

import config.Constants
import models.core.{AppModelT, ModelIdImplT, ModelIdT}
import models.elasticsearch.MappingName
import org.joda.time.DateTime
import utils.JsonUtils

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
    this(Constants.DEFAULT_ID, Constants.DEFAULT_VERSION, new DateTime(), new DateTime(),
      indexId,
      name
    )
  }

  override def toString(): String = {
    JsonUtils.toJson(this)
  }
}

