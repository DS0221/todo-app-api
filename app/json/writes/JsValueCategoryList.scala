package json.writes

import model.CategoryForm
import play.api.libs.json.{Json, Writes}

case class JsValueCategoryList(
  categoryList: Seq[JsValueCategoryListItem]
)

object JsValueCategoryList {
  implicit val writes: Writes[JsValueCategoryList] = Json.writes[JsValueCategoryList]

  def apply(seq: Iterable[CategoryForm]): JsValueCategoryList =
    JsValueCategoryList(
      categoryList = seq.toSeq.map(v => JsValueCategoryListItem.apply(v))
    )
}