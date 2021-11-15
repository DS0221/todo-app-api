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

  def list() = Action { implicit req =>

    val categoryListFuture = for {
      categories <- CategoryRepository.getAll()
    } yield {
      categories.map(
        category =>
          CategoryForm(
            category.v.name,
            category.v.slug,
            category.v.color.name,
            category.id
          )
      )
    }

    val categoryList = Await.ready(categoryListFuture, Duration.Inf).value.get.toEither.getOrElse(Seq.empty)

    val vv = ViewValueCategoryList(
      title  = "カテゴリー一覧",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js"),
      categoryList = categoryList
    )

    Ok(views.html.CategoryList(vv))
  }

}
