package json.writes

import play.api.libs.json.Writes
import play.api.libs.json.Json
import lib.model.Category

case class JsValueCategoryColor(
    code:   Short,
    name:   String
)

object JsValueCategoryColor {
  implicit val writes: Writes[JsValueCategoryColor] = Json.writes[JsValueCategoryColor]

  def apply(color: Category.CategoryColor): JsValueCategoryColor =
      JsValueCategoryColor(
          code          = color.code,
          name          = color.name
      )
}
