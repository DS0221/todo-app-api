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
import play.api.libs.json.Json
import json.writes.JsValueCategoryList
import json.writes.JsValueCategoryColorList
import json.reads.JsValueCreateCategory
import json.writes.JsValueDeleteCategory
import json.writes.JsValueSelectCategory
import json.reads.JsValueUpdateCategory



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
        val categoryList = categories.map(
          category =>
            CategoryForm(
              category.v.name,
              category.v.slug,
              category.v.color.name,
              category.id
            )    
        )
        val jsValue = JsValueCategoryList.apply(categoryList)
        Ok(Json.toJson(jsValue))
      }
    )
  }

  def categoryColorList() = Action {
    val colors = Category.CategoryColor.values
    val jsValue = JsValueCategoryColorList.apply(colors)
    Ok(Json.toJson(jsValue))
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

  def newCategorySave() = Action(parse.json).async { implicit req =>

    req.body.validate[JsValueCreateCategory].fold(
      errors => {
        Future.successful{
          BadRequest("error")
        }
      },
      categoryData => {
        for {
           newCategory <- CategoryRepository.add(Category(name = categoryData.name, slug = categoryData.slug, color = Category.CategoryColor(categoryData.color.toShort)))
         } yield {
           val jsValue = JsValueCreateCategory.apply(categoryData.name, categoryData.slug, categoryData.color)
           Ok(Json.toJson(jsValue))
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
      val jsValue = JsValueDeleteCategory.apply(deleteCategory.get.id)
      Ok(Json.toJson(jsValue))
    }
  }

  def editCategory(categoryId : Long) = Action.async { implicit req =>
    
    val categoryFuture = CategoryRepository.get(Category.Id(categoryId))
    
    for {
      category <- categoryFuture
    }yield {
      val categoryData = category.get.v
      val categoryEdit = CategoryEdit.apply(
        categoryData.name,
        categoryData.slug,
        categoryData.color
      )

      val jsValue = JsValueSelectCategory.apply(categoryEdit)
      Ok(Json.toJson(jsValue))
    }
  }

  def editCategorySave() = Action(parse.json).async { implicit req =>

    req.body.validate[JsValueUpdateCategory].fold(
      errors => {
        Future.successful{
          BadRequest("error")
        }
      },
      inputData => {
        for {
           editCategory <- CategoryRepository.update(Category(id=inputData.id, slug = inputData.slug, name = inputData.name, color = Category.CategoryColor(inputData.color.toShort)))
         } yield {
           val jsValue = JsValueUpdateCategory.apply(inputData.name, inputData.slug, inputData.color, inputData.id)
           Ok(Json.toJson(jsValue))
         }
      }
    )
  }
}
