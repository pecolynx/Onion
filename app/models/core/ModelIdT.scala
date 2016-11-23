package models.core

/**
  * モデルId。
  *
  * @tparam T
  */
trait ModelIdT[T] {
  def value: T
}
