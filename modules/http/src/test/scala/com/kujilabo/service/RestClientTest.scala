package com.kujilabo.service

import com.kujilabo.util.HttpClientUtils
import org.scalatest._

class RestClientTest extends FunSpec with ShouldMatchers {
  describe("head") {
    describe("接続できるとき") {
      it("Okが返ること") {
        HttpClientUtils.isOk(RestClient.head("http://kujilabo.com:9000")) should equal(true)
      }
    }
  }
}