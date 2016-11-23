package viewmodels.file

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import models.core.BaseObject
import viewmodels.{VmCheckAuthParameter, VmDocumentFile}

/**
  * ファイル追加パラメータ
  *
  * @param documentFile
  */
@JsonIgnoreProperties(ignoreUnknown = true)
class VmFileAddParameter
(
  val documentFile: VmDocumentFile
) extends BaseObject {

}
