package models.elasticsearch

import config.Constants
import org.joda.time.DateTime
import com.kujilabo.common._
import com.kujilabo.models.core._

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
