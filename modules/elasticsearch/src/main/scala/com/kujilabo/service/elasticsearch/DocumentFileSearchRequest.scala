package com.kujilabo.service.elasticsearch

object DocumentFileSearchRequest {

  def getQuery(keyword: String): Map[String, Any] = {
    keyword match {
      case "" =>
        Map("match_all" -> Map.empty)
      case _ =>
        Map("multi_match" ->
          Map(
            "query" -> keyword,
            "fields" -> List("file_content", "file_path"),
            "operator" -> "and"
          )
        )
    }
  }

  def getFilter(keyword: String, createdBy: Int): Map[String, Any] = {
    val keywordQuery = Map("or" ->
      List(
        Map("query" -> Map(
          "match" -> Map(
            "file_path" -> Map(
              "query" -> keyword,
              "operator" -> "and"
            )
          )
        )),
        Map("query" -> Map(
          "match" -> Map(
            "file_content" -> Map(
              "query" -> keyword,
              "operator" -> "and"
            )
          )
        ))
      )
    )

    val createdByQuery = Map("term" -> Map(
      "created_by" -> createdBy
    ))

    val list = keyword match {
      case "" =>
        List(
          createdByQuery
        )
      case _ =>
        List(
          keywordQuery,
          createdByQuery
        )
    }

    Map(
      "and" -> list
    )
  }

  def getFields(): List[String] = {
    List("title", "file_path", "file_created_at",
      "file_updated_at", "created_at", "updated_at", "created_by")
  }

  def getMap(createdBy: Int, keyword: String, from: Int, size: Int): Map[String, Any] = {
    Map(
      "fields" -> this.getFields(),
      "query" -> this.getQuery(keyword),
      "filter" -> this.getFilter(keyword, createdBy),
      "highlight" -> Map(
        "pre_tags" -> List("<mark>"),
        "post_tags" -> List("</mark>"),
        "fields" -> Map(
          "title" -> Map.empty[String, String],
          "file_content" -> Map.empty[String, String]
        )
      ),
      "from" -> from,
      "size" -> size,
      "version" -> true
    )
  }
}
