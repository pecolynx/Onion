package com.kujilabo.models.core

import javax.validation.constraints.{Max, Min}

import scala.annotation.meta.field

/**
  * ページ。
  *
  * @param page ページ番号
  * @param size 1ページあたりの件数
  */
class Page
(
  @(Min@field)(value = 1)
  val page: Int,
  @(Min@field)(value = 1)
  @(Max@field)(value = 1000)
  val size: Int
) extends BaseObject {
  this.validate
}
