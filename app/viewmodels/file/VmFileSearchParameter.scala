package viewmodels.file

import com.kujilabo.models.core._
import viewmodels.{VmCheckAuthParameter, VmDocumentFile}

class VmFileSearchParameter
(
  val checkAuth: VmCheckAuthParameter,
  val searchCondition: VmSearchCondition
) extends BaseObject {

}
