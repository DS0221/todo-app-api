package json.writes

import model.ToDoForm
import play.api.libs.json.{Json, Writes}

case class JsValueTodoList(
  todoList: Seq[JsValueTodoListItem]
)

object JsValueTodoList {
  implicit val writes: Writes[JsValueTodoList] = Json.writes[JsValueTodoList]

  def apply(seq: Iterable[ToDoForm]): JsValueTodoList =
    JsValueTodoList(
      todoList = seq.toSeq.map(v => JsValueTodoListItem.apply(v))
    )
}