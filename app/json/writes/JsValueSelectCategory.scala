package json.writes

import play.api.libs.json.Writes
import play.api.libs.json.Json
import model.CategoryEdit

case class JsValueSelectCategory(
    name:    String,
    slug:    String,
    color:   Short
)

object JsValueSelectCategory {
  implicit val writes: Writes[JsValueSelectCategory] = Json.writes[JsValueSelectCategory]

  def apply(categoryEdit: CategoryEdit): JsValueSelectCategory =
      JsValueSelectCategory(
          name  = categoryEdit.name,
          slug  = categoryEdit.slug,
          color = categoryEdit.color.code
      )
}
