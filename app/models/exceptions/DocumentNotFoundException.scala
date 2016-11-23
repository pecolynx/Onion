package models.exceptions

class DocumentNotFoundException
(
  message: String,
  val documentId: String
) extends ModelNotFoundException(message) {

}
