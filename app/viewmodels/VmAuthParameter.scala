package viewmodels

import models.core.BaseObject

/**
  * 認証クラス。
  */
class VmAuthParameter
(
  val loginId: String,
  val loginPassword: String
) extends BaseObject {

}
