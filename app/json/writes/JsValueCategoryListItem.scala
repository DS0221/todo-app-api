package json.writes

import play.api.libs.json.Writes
import play.api.libs.json.Json
import model.CategoryForm

case class JsValueCategoryListItem(
    name:           String,
    slug:           String,
    color:          String,
    id:             Long
)

object JsValueCategoryListItem {
  implicit val writes: Writes[JsValueCategoryListItem] = Json.writes[JsValueCategoryListItem]

  def apply(categoryListItem: CategoryForm): JsValueCategoryListItem =
      JsValueCategoryListItem(
          name          = categoryListItem.name,
          slug          = categoryListItem.slug,
          color         = categoryListItem.color,
          id            = categoryListItem.id
      )
}
