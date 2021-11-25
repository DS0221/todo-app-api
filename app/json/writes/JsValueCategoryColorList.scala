package json.writes

import model.CategoryForm
import play.api.libs.json.{Json, Writes}
import lib.model.Category

case class JsValueCategoryColorList(
  categoryColorList: Seq[JsValueCategoryColor]
)

object JsValueCategoryColorList {
  implicit val writes: Writes[JsValueCategoryColorList] = Json.writes[JsValueCategoryColorList]

  def apply(list: List[Category.CategoryColor]): JsValueCategoryColorList =
    JsValueCategoryColorList(
      categoryColorList = list.toSeq.map(v => JsValueCategoryColor.apply(v))
    )
}