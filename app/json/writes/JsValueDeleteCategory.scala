package json.writes

import play.api.libs.json.Json
import play.api.libs.json.Writes

case class JsValueDeleteCategory(
    id:      Long
)

object JsValueDeleteCategory {
    implicit val writes: Writes[JsValueDeleteCategory] = Json.writes[JsValueDeleteCategory]
}
