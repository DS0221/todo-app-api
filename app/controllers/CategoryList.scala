/**
 *
 * to do sample project
 *
 */

package controllers

import javax.inject._
import play.api.mvc._

import model.ViewValueHome
import lib.persistence.onMySQL._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import model.ViewValueToDoList
import model.ViewValueCategoryList

case class CategoryForm(
    name:           String,
    slug:           String,
    color:          String,
    id:             Long
)

@Singleton
class CategoryListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def list() = Action.async {

    CategoryRepository.getAll().map(
      categories => {
        val vv = ViewValueCategoryList(
          title  = "カテゴリー一覧",
          cssSrc = Seq("main.css"),
          jsSrc  = Seq("main.js"),
          categoryList = categories.map(
            category =>
            CategoryForm(
              category.v.name,
              category.v.slug,
              category.v.color.name,
              category.id
            )    
          )
        )
        Ok(views.html.CategoryList(vv))
      }
    )
  }
}
