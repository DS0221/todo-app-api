/**
  * This is a sample of Todo Application.
  * 
  */

package lib.persistence.db

import slick.jdbc.JdbcProfile

// Tableを扱うResourceのProvider
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
trait SlickResourceProvider[P <: JdbcProfile] {

  implicit val driver: P
  object UserTable extends UserTable
  object ToDoTable extends ToDoTable
  object CategoryTable extends CategoryTable
  // --[ テーブル定義 ] --------------------------------------
  lazy val AllTables = Seq(
    UserTable,ToDoTable,CategoryTable
  )
}
