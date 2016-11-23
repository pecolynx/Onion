package viewmodels.file

import models.core.BaseObject
import viewmodels.VmCheckAuthParameter

class VmFileListParameter
(
  val checkAuth: VmCheckAuthParameter,
  val page: VmPage
) extends BaseObject {

}
