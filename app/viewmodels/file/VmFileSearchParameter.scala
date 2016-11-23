package viewmodels.file

import models.core.BaseObject
import viewmodels.{VmCheckAuthParameter, VmDocumentFile}

class VmFileSearchParameter
(
  val checkAuth: VmCheckAuthParameter,
  val searchCondition: VmSearchCondition
) extends BaseObject {

}
