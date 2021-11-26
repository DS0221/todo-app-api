package json.reads

import play.api.libs.json.Reads
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class JsValueCreateTodo(
    title:      String,
    body:       String,
    category:   Long
)

object JsValueCreateTodo {
    implicit val reads: Reads[JsValueCreateTodo] = Json.reads[JsValueCreateTodo]
    implicit val writes: Writes[JsValueCreateTodo] = Json.writes[JsValueCreateTodo]
  
}
