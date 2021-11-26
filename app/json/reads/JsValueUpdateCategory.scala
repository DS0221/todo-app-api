package json.reads

import play.api.libs.json.Reads
import play.api.libs.json.Json
import play.api.libs.json.Writes

case class JsValueUpdateCategory(
    name:    String,
    slug:    String,
    color:   String,
    id:      Long
)

object JsValueUpdateCategory {
    implicit val reads: Reads[JsValueUpdateCategory] = Json.reads[JsValueUpdateCategory]
    implicit val writes: Writes[JsValueUpdateCategory] = Json.writes[JsValueUpdateCategory]
  
}
