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
import model.ViewValueCategoryList
import model.ViewValueCategoryNew
import play.api.data.Form
import play.api.data.Forms._
import play.filters.csrf.CSRF
import model.Category
import akka.http.scaladsl.model.HttpHeader
import scala.concurrent.Future

case class CategoryForm(
    name:           String,
    slug:           String,
    color:          String,
    id:             Long
)

case class CategoryNew(
  name:      String,
  slug:      String,
  color:     Short
)

@Singleton
class CategoryListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with play.api.i18n.I18nSupport{

  val colorMap = Category.CategoryColor.values.map(value => (value.code.toString(),value.name))

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

  def newCategory() = Action { implicit req =>
    implicit val token = CSRF.getToken(req).get

    val categoryNewForm = Form (
      mapping(
        "name" -> nonEmptyText,
        "slug" -> nonEmptyText,
        "color" -> shortNumber
      )(CategoryNew.apply)(CategoryNew.unapply)
    )
    
    val vv = ViewValueCategoryNew(
      title  = "カテゴリー新規登録",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js"),
      inputForm = categoryNewForm
    )

    Ok(views.html.NewCategory(vv,colorMap))
  }

  def newCategorySave() = Action.async { implicit req =>

    implicit val token = CSRF.getToken(req).get

    val categoryNewForm = Form (
      mapping(
        "name" -> nonEmptyText,
        "slug" -> nonEmptyText,
        "color" -> shortNumber
      )(CategoryNew.apply)(CategoryNew.unapply)
    )

    categoryNewForm.bindFromRequest().fold(
      formWithErrors => {
        Future {
          println(categoryNewForm.data)
          BadRequest(views.html.NewCategory(ViewValueCategoryNew(
            title  = "カテゴリー新規登録",
            cssSrc = Seq("main.css"),
            jsSrc  = Seq("main.js"),
            inputForm = formWithErrors
          ), colorMap))
        }
      },
      inputData => {
        for {
          newCategory <- CategoryRepository.add(Category(name = inputData.name, slug = inputData.slug, color = Category.CategoryColor(inputData.color)))
        } yield {
          Redirect(routes.CategoryListController.list())
        }
      }
    )
  }
}
