package controllers

import com.kujilabo.models.core.{ModelIdImplT, ModelIdT, VariableName}
import com.kujilabo.models.elasticsearch.es.{EsDocument, EsDocumentField, EsDocumentFieldList}
import com.kujilabo.models.elasticsearch.{IndexName, MappingName, Title}
import com.kujilabo.util.JsonUtils
import models.AppSettingsImpl
import models.core._
import org.joda.time.DateTime
import org.scalatest._
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import viewmodels.file.VmFileAddParameter

class DocumentFileControllerTest_Base extends FunSpec with BeforeAndAfter
  with ShouldMatchers with MockitoSugar {

  val t = spy(new DocumentFileController(new AppSettingsImpl))
  val APP_USER_ID = new ModelIdImplT[Int](456)
  val FILE_ID = new ModelIdImplT[Int](789)
  val CREATED_BY = 456
  val VM_FILE = new VmDocumentFile("abc.txt", "C:\\abc.txt", "ABC", "", 123,
    "2000/01/02 03:04:05", "2001/01/02 03:04:05")
  val VM_ADD_FILE = new VmFileAddParameter(VM_FILE)
  val DOCUMENT_ID = new ModelIdImplT[String]("abc")
  val INDEX_NAME = new IndexName(new VariableName("test_index"))
  val MAPPING_NAME = new MappingName(new VariableName("test_mapping"))
  val TITLE = new Title("title")
  val FIELD_LIST = new EsDocumentFieldList(List.empty[EsDocumentField])

  before {
    // stub getToken
    val token = Some(new AppUserAuthToken(new ModelIdImplT[Int](123), "abc", new DateTime()))
    doReturn(token).when(t).getToken(any[String])

    // stub getUser
    val user = Some(new AppUser(APP_USER_ID, 1, new DateTime(), new DateTime(),
      "loginId", "loginPassword", "email", "name"))
    doReturn(user).when(t).getUser(any[ModelIdT[Int]])

    val document = Some(new EsDocument(DOCUMENT_ID, 1, new DateTime(), new DateTime(),
      INDEX_NAME, MAPPING_NAME, TITLE, CREATED_BY, FIELD_LIST))
    doReturn(document).when(t).getDocument(any[ModelIdT[String]])

    // stub
    doReturn(false).when(t).containsFile(any[ModelIdT[Int]], any[String], any[String])

    // stub
    doReturn(FILE_ID).when(t).addFile(any[ModelIdT[Int]], any[String], any[VmDocumentFile])

    // stub
    doReturn(FILE_ID).when(t).updateFile(any[ModelIdT[Int]], any[String], any[VmDocumentFile])

    // stub
    doNothing().when(t).removeDocument(any[ModelIdT[String]])

    // stub clearFile
    doNothing().when(t).clearFile(any[ModelIdT[Int]], any[String])
  }

}

class DocumentFileControllerTest_Clear extends DocumentFileControllerTest_Base {
  val request = FakeRequest(POST, "/document/file/clear?authToken=123").
    withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")

  describe("file_clear") {
    describe("正常に処理されたとき") {
      it("200が返ること") {
        val result = t.file_clear(request)
        status(result) should equal(OK)
        verify(t).clearFile(APP_USER_ID, "")
      }
    }

    describe("トークンが未登録のとき") {
      it("401が返ること") {
        doReturn(None).when(t).getToken(any[String])
        val result = t.file_clear(request)
        status(result) should equal(UNAUTHORIZED)
      }
    }

    describe("ユーザーが未登録のとき") {
      it("401が返ること") {
        doReturn(None).when(t).getUser(any[ModelIdT[Int]])
        val result = t.file_clear(request)
        status(result) should equal(UNAUTHORIZED)
      }
    }

    describe("トークンが渡されなかったとき") {
      it("400が返ること") {
        val request = FakeRequest(POST, "/document/file/clear").
          withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
        val result = t.file_clear(request)
        status(result) should equal(BAD_REQUEST)
      }
    }
  }
}

class DocumentFileControllerTest_Add extends DocumentFileControllerTest_Base {

  val request = FakeRequest(POST, "/document/file/add?authToken=123").
    withHeaders(HeaderNames.CONTENT_TYPE -> "application/json").
    withJsonBody(Json.parse(JsonUtils.toJson(VM_ADD_FILE)))

  describe("file_add") {
    describe("追加されたとき") {
      describe("正常に処理されたとき") {
        it("200が返ること") {
          val result = t.file_add(request)
          status(result) should equal(OK)
          verify(t).addFile(any[ModelIdT[Int]], any[String], any[VmDocumentFile])
        }
      }
    }
    describe("更新されたとき") {
      describe("正常に処理されたとき") {
        it("200が返ること") {
          doReturn(true).when(t).containsFile(any[ModelIdT[Int]], any[String], any[String])
          val result = t.file_add(request)
          status(result) should equal(OK)
          verify(t).updateFile(any[ModelIdT[Int]], any[String], any[VmDocumentFile])
        }
      }
    }

    describe("トークンが未登録のとき") {
      it("401が返ること") {
        doReturn(None).when(t).getToken(any[String])
        val result = t.file_add(request)
        status(result) should equal(UNAUTHORIZED)
      }
    }

    describe("ユーザーが未登録のとき") {
      it("401が返ること") {
        doReturn(None).when(t).getUser(any[ModelIdT[Int]])
        val result = t.file_add(request)
        status(result) should equal(UNAUTHORIZED)
      }
    }

    describe("トークンが渡されなかったとき") {
      it("400が返ること") {
        val request = FakeRequest(POST, "/document/file/add").
          withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
        val result = t.file_add(request)
        status(result) should equal(BAD_REQUEST)
      }
    }
  }
}


class DocumentFileControllerTest_Remove extends DocumentFileControllerTest_Base {

  val request = FakeRequest(DELETE, "/document/file/remove?authToken=123").
    withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")

  describe("file_remove") {
    describe("削除されたとき") {
      describe("正常に処理されたとき") {
        it("200が返ること") {
          val result = t.file_remove("123")(request)
          status(result) should equal(OK)
          verify(t).removeDocument(any[ModelIdT[String]])
        }
      }
    }

    describe("トークンが未登録のとき") {
      it("401が返ること") {
        doReturn(None).when(t).getToken(any[String])
        val result = t.file_remove("123")(request)
        status(result) should equal(UNAUTHORIZED)
      }
    }

    describe("ユーザーが未登録のとき") {
      it("401が返ること") {
        doReturn(None).when(t).getUser(any[ModelIdT[Int]])
        val result = t.file_remove("123")(request)
        status(result) should equal(UNAUTHORIZED)
      }
    }

    describe("トークンが渡されなかったとき") {
      it("400が返ること") {
        val request = FakeRequest(POST, "/document/file/add").
          withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
        val result = t.file_remove("123")(request)
        status(result) should equal(BAD_REQUEST)
      }
    }
  }
}