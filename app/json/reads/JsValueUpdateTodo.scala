package json.reads

import play.api.libs.json.Reads
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class JsValueUpdateTodo(
    title:      String,
    body:       String,
    category:   String,
    state:      String,
    id:         Long
)

object JsValueUpdateTodo {
    implicit val reads: Reads[JsValueUpdateTodo] = Json.reads[JsValueUpdateTodo]
    implicit val writes: Writes[JsValueUpdateTodo] = Json.writes[JsValueUpdateTodo]
  
}
