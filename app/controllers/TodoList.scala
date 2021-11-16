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
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import model.ViewValueToDoList
import play.api.data._
import play.api.data.Forms._
import akka.http.scaladsl.model.HttpHeader
import model.ViewValueToDoNew
import play.filters.csrf.CSRF
import ixias.model.Entity
import model.ToDo
import monix.execution.misc.AsyncVar
import model.Category
import cats.instances.long

case class ToDoForm(
    categoryName:   Option[String],
    title:          String,
    body:           String,
    state:          String,
    categoryColor:  Option[String],
    id:             Long
)

case class ToDoNew(
  title:      String,
  body:       String,
  category:   Long
)

@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with play.api.i18n.I18nSupport{

  val todoInputForm = Form (
    mapping(
      "title" -> nonEmptyText,
      "body" -> text,
      "category" -> longNumber
    )(ToDoNew.apply)(ToDoNew.unapply)
  )

  def list() = Action.async {
    for {
      todos <- ToDoRepository.getAll()
      categories <- CategoryRepository.getAll()
    } yield {
      val todoList = for {
        todo <- todos
      } yield {
        val category = categories.find(_.id == todo.v.categoryId)
        ToDoForm(
          category.map(_.v.name),
          todo.v.title,
          todo.v.body,
          todo.v.state.name,
          category.map(_.v.color.name),
          todo.id
        )
      }

      val vv = ViewValueToDoList(
        title  = "Todo一覧",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js"),
        toDoList = todoList
      )
      Ok(views.html.TodoList(vv))
    }
  }

  def newTodo() = Action.async { implicit req =>
    implicit val token = CSRF.getToken(req).get
    
    val vv = ViewValueToDoNew(
      title  = "Todo新規登録",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js"),
      inputForm = todoInputForm
    )
    for {
      categories <- CategoryRepository.getAll().map(categories => categories.map(category => (category.id.toString(),category.v.name)))
    }yield {
      Ok(views.html.NewTodo(vv,categories))
    }
 
  }

  def newTodoSave() = Action.async { implicit req =>

    implicit val token = CSRF.getToken(req).get
    todoInputForm.bindFromRequest().fold(
      formWithErrors => {
        for {
          categories <- CategoryRepository.getAll().map(categories => categories.map(category => (category.id.toString(),category.v.name)))
        }yield {
          BadRequest(views.html.NewTodo(ViewValueToDoNew(
            title  = "Todo新規登録",
            cssSrc = Seq("main.css"),
            jsSrc  = Seq("main.js"),
            inputForm = formWithErrors
          ), categories))
        }
      },
      inputData => {
        for {
          newTodo <- ToDoRepository.add(ToDo(title = inputData.title, body = inputData.body, categoryId = inputData.category))
        } yield {
          Redirect(routes.TodoListController.list())
        }
      }
    )
  }

}
