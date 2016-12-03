package viewmodels.file

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kujilabo.models.core.BaseObject
import viewmodels.{VmDocumentFile}

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
