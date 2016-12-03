package viewmodels.file

import com.kujilabo.models.core._
import viewmodels.VmCheckAuthParameter

class VmFileListParameter
(
  val checkAuth: VmCheckAuthParameter,
  val page: VmPage
) extends BaseObject {

}
