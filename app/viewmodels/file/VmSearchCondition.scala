package viewmodels.file

import javax.validation.constraints.{Max, Min}

import config.Constants
import com.kujilabo.models.core._

import scala.annotation.meta.field

class VmSearchCondition
(
  @(Min@field)(value = Constants.PAGE_NUMBER_VALUE_MIN)
  val pageNumber: Int,
  @(Min@field)(value = Constants.PAGE_SIZE_VALUE_MIN)
  @(Max@field)(value = Constants.PAGE_SIZE_VALUE_MAX)
  val pageSize: Int,
  val keywordList: List[String],
  val extList: List[String]
) extends BaseObject {

}
