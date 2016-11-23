package viewmodels.file

import models.core.BaseObject
import viewmodels.{VmCheckAuthParameter, VmDocumentFile}

class VmFileUpdateParameter
(
  val checkAuth: VmCheckAuthParameter,
  val documentFile: VmDocumentFile
) extends BaseObject {


}
