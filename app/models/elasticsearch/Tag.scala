package models.elasticsearch

import config.Constants
import models.core.{AppModelT, ModelIdImplT, ModelIdT, VariableName}
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
      Constants.DEFAULT_ID, Constants.DEFAULT_VERSION, new DateTime(), new DateTime(),
      name,
      multiLangName
    )
  }
}
