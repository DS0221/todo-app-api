package json.writes

import play.api.libs.json.Writes
import play.api.libs.json.Json
import model.ToDoEdit

case class JsValueSelectTodo(
    title:      String,
    body:       String,
    category:   Long,
    state:      Short
)

object JsValueSelectTodo {
  implicit val writes: Writes[JsValueSelectTodo] = Json.writes[JsValueSelectTodo]

  def apply(todoEdit: ToDoEdit): JsValueSelectTodo =
      JsValueSelectTodo(
          title         = todoEdit.title,
          body          = todoEdit.body,
          state         = todoEdit.state.code,
          category      = todoEdit.category
      )
}
