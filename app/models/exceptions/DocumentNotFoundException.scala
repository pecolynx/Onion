package models.exceptions

import com.kujilabo.common.ModelNotFoundException

class DocumentNotFoundException
(
  message: String,
  val documentId: String
) extends ModelNotFoundException(message) {

}
