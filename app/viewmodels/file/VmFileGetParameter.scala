package viewmodels.file

import com.kujilabo.models.core._
import viewmodels.{VmCheckAuthParameter, VmDocumentFile}

class VmFileGetParameter
(
  val checkAuth: VmCheckAuthParameter,
  val documentFile: VmDocumentFile
) extends BaseObject {


}
