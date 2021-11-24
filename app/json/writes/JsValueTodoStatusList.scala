package json.writes

import model.CategoryForm
import play.api.libs.json.{Json, Writes}
import lib.model.ToDo

case class JsValueTodoStatusList(
  todoStatusList: Seq[JsValueTodoStatus]
)

object JsValueTodoStatusList {
  implicit val writes: Writes[JsValueTodoStatusList] = Json.writes[JsValueTodoStatusList]

  def apply(list: List[ToDo.Status]): JsValueTodoStatusList =
    JsValueTodoStatusList(
      todoStatusList = list.toSeq.map(v => JsValueTodoStatus.apply(v))
    )
}