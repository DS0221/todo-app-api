package json.reads

import play.api.libs.json.Reads
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class JsValueCreateCategory(
    name:    String,
    slug:    String,
    color:   String
)

object JsValueCreateCategory {
    implicit val reads: Reads[JsValueCreateCategory] = Json.reads[JsValueCreateCategory]
    implicit val writes: Writes[JsValueCreateCategory] = Json.writes[JsValueCreateCategory]
  
}
