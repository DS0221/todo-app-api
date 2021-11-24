package json.writes

import play.api.libs.json.Writes
import play.api.libs.json.Json
import lib.model.ToDo

case class JsValueTodoStatus(
    code:   Short,
    name:   String
)

object JsValueTodoStatus {
  implicit val writes: Writes[JsValueTodoStatus] = Json.writes[JsValueTodoStatus]

  def apply(todoStatus: ToDo.Status): JsValueTodoStatus =
      JsValueTodoStatus(
          code          = todoStatus.code,
          name          = todoStatus.name
      )
}
