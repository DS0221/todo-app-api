package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime
// categoryModel
import Category._
case class Category(
    id:             Option[Id],
    name:          String,
    slug:           String,
    color:          CategoryColor,
    updatedAt:      LocalDateTime = NOW,
    createdAt:      LocalDateTime = NOW
) extends EntityModel[Id]

object Category {

  // カテゴリカラー定義
  sealed abstract class CategoryColor(val code: Short, val name: String) extends EnumStatus
  object CategoryColor extends EnumStatus.Of[CategoryColor] {
    case object RED extends CategoryColor(code = 1, name = "RED")
    case object BLUE extends CategoryColor(code = 2, name = "BLUE")
    case object GRAY extends CategoryColor(code = 3, name = "GRAY")
    case object ORANGE extends CategoryColor(code = 4, name = "ORANGE")
    case object PURPLE extends CategoryColor(code = 5, name = "PURPLE")
    case object WHITE extends CategoryColor(code = 6, name = "WHITE")
    case object SALMON extends CategoryColor(code = 7, name = "SALMON")
  }

  

  val  Id = the[Identity[Id]]
  type Id = Long @@ Category
  type WithNoId = Entity.WithNoId [Id, Category]
  type EmbeddedId = Entity.EmbeddedId[Id, Category]

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(name: String, slug: String, color: CategoryColor): WithNoId = {
    new Entity.WithNoId(
      new Category(
        id    = None,
        name = name,
        slug = slug,
        color = color
      )
    )
  }

  // 更新時
  def apply(id: Long, name: String, slug: String, color: CategoryColor) : EmbeddedId = {
    new Entity.EmbeddedId(
      new Category(
        id = Some(Category.Id(id)),
        name = name,
        slug = slug,
        color = color
      )
    )
  }

}
