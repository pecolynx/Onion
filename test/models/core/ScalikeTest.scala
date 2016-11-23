package models.core

import org.joda.time.{DateTime, LocalDate}
import org.scalatest.WordSpec
import scalikejdbc.{ConnectionPool, DB, SQL}

class ScalikeTest extends WordSpec {
  "" when {
    "" should {
      "" in {
        Class.forName("org.h2.Driver")
        ConnectionPool.singleton("jdbc:h2:mem:scalikejdbc", "user", "pass")

        DB.autoCommit { implicit session =>
          SQL(
            """
              create table members(
              id bigint primary key auto_increment,
              name varchar(30) not null,
              description varchar(1000),
              birthday date,
              created_at timestamp not null
              )
            """).execute.apply()
        }

        DB.localTx { implicit session =>
          val insertSql = SQL("insert into members(name, birthday, created_at) values(?, ?, ?)")
          val createdAt = DateTime.now
          insertSql.bind("Alice", Option(new LocalDate("1980-01-01")),
            createdAt).update.apply()
          insertSql.bind("Bob", None, createdAt).update.apply()
        }

        val members: List[Map[String, Any]] = DB.readOnly { implicit session =>
          SQL("select * from members").map(rs => rs.toMap).list.apply()
        }

        members.foreach(println)

        println("end")
      }
    }
  }

}
