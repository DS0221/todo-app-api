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
import model.ViewValueCategoryEdit
import model.CategoryForm
import model.CategoryNew
import model.CategoryEdit
import play.api.data.Form
import play.api.data.Forms._
import play.filters.csrf.CSRF
import lib.model.Category
import akka.http.scaladsl.model.HttpHeader
import scala.concurrent.Future



@Singleton
class CategoryListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with play.api.i18n.I18nSupport{

  val colorMap = Category.CategoryColor.values.map(value => (value.code.toString(),value.name))

  val categoryNewForm = Form (
      mapping(
        "name" -> nonEmptyText,
        "slug" -> nonEmptyText.verifying("英数字のみ", {slug => slug.matches("^[a-zA-Z0-9]*$")}),
        "color" -> shortNumber.transform[Category.CategoryColor](Category.CategoryColor.apply, _.code)
      )(CategoryNew.apply)(CategoryNew.unapply)
    )

  val categoryEditForm = Form (
      mapping(
        "name" -> nonEmptyText,
        "slug" -> nonEmptyText.verifying("英数字のみ", {slug => slug.matches("^[a-zA-Z0-9]*$")}),
        "color" -> shortNumber.transform[Category.CategoryColor](Category.CategoryColor.apply, _.code)
      )(CategoryEdit.apply)(CategoryEdit.unapply)
    )

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

    categoryNewForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful {
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
          newCategory <- CategoryRepository.add(Category(name = inputData.name, slug = inputData.slug, color = inputData.color))
        } yield {
          Redirect(routes.CategoryListController.list())
        }
      }
    )
  }

  def deleteCategory(categoryId : Long) = Action.async { implicit req =>
    val deleteFuture = CategoryRepository.remove(Category.Id(categoryId))

    val deleteTodoFuture = ToDoRepository.removeFromCategory(categoryId)

    for {
      deleteCategory <- deleteFuture
      deleteTodo <- deleteTodoFuture
    }yield {
      Redirect(routes.CategoryListController.list())
    }
  }

  def editCategory(categoryId : Long) = Action.async { implicit req =>
    implicit val token = CSRF.getToken(req).get
    
    val categoryFuture = CategoryRepository.get(Category.Id(categoryId))
    
    for {
      category <- categoryFuture
    }yield {
      val categoryData = category.get.v
      val categoryMap = Map(
        "name" -> categoryData.name,
        "slug"  -> categoryData.slug,
        "color" -> categoryData.color.code.toString()
      )
      val vv = ViewValueCategoryEdit(
        title  = "カテゴリー修正",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js"),
        inputForm = categoryEditForm.bind(categoryMap),
        categoryId
      )
      Ok(views.html.EditCategory(vv,colorMap))
    }
 
  }

  def editCategorySave(categoryId : Long) = Action.async { implicit req =>
    implicit val token = CSRF.getToken(req).get

    categoryEditForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful {
          BadRequest(views.html.EditCategory(ViewValueCategoryEdit(
              title  = "カテゴリー修正",
              cssSrc = Seq("main.css"),
              jsSrc  = Seq("main.js"),
              inputForm = formWithErrors,
              categoryId
            ), colorMap))
        }
          
      },
      inputData => {
        for {
          editCategory <- CategoryRepository.update(Category(id=categoryId, name = inputData.name, slug = inputData.slug, color=inputData.color))
        } yield {
          Redirect(routes.CategoryListController.list())
        }
      }
    )
  }
}
