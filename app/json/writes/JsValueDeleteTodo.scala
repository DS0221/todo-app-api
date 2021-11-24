package json.writes

import play.api.libs.json.Json
import play.api.libs.json.Writes

case class JsValueDeleteTodo(
    id:      Long
)

object JsValueDeleteTodo {
    implicit val writes: Writes[JsValueDeleteTodo] = Json.writes[JsValueDeleteTodo]
}
