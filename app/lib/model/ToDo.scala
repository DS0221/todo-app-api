package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// toDoModel
import ToDo._
case class ToDo(
    id:             Option[Id],
    categoryId :    Long,
    title:          String,
    body:           String,
    state:          Status,
    updatedAt:      LocalDateTime = NOW,
    createdAt:      LocalDateTime = NOW
) extends EntityModel[Id]

object ToDo {

  val  Id = the[Identity[Id]]
  type Id = Long @@ ToDo
  type WithNoId = Entity.WithNoId [Id, ToDo]
  type EmbeddedId = Entity.EmbeddedId[Id, ToDo]

  // ステータス定義
  sealed abstract class Status(val code: Short, val name: String) extends EnumStatus
  object Status extends EnumStatus.Of[Status] {
    case object IS_BEFORE extends Status(code = 0, name = "TODO(着手前)")
    case object IS_INPROGRESS extends Status(code = 1, name = "進行中")
    case object IS_COMPLETE extends Status(code = 2, name = "完了")
  }

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(title: String, body: String, categoryId: Long): WithNoId = {
    new Entity.WithNoId(
      new ToDo(
        id    = None,
        categoryId = categoryId,
        title = title,
        body = body,
        state = Status.IS_BEFORE
      )
    )
  }

  // 更新時
  def apply(id: Long, title: String, body: String, categoryId: Long, state: Status) : EmbeddedId = {
    new Entity.EmbeddedId(
      new ToDo(
        id = Some(ToDo.Id(id)),
        categoryId = categoryId,
        title = title,
        body = body,
        state = state
      )
    )
  }
}
