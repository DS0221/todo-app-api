package json.writes

import play.api.libs.json.Writes
import play.api.libs.json.Json
import model.ToDoForm

case class JsValueTodoListItem(
    categoryName:   Option[String],
    title:          String,
    body:           String,
    state:          String,
    categoryColor:  Option[String],
    id:             Long
)

object JsValueTodoListItem {
  implicit val writes: Writes[JsValueTodoListItem] = Json.writes[JsValueTodoListItem]

  def apply(todoListItem: ToDoForm): JsValueTodoListItem =
      JsValueTodoListItem(
          categoryName  = todoListItem.categoryName,
          title         = todoListItem.title,
          body          = todoListItem.body,
          state         = todoListItem.state,
          categoryColor = todoListItem.categoryColor,
          id            = todoListItem.id
      )
}
