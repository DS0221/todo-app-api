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
import model.ViewValueToDoEdit
import model.ToDoForm
import model.ToDoEdit
import model.ToDoNew
import play.filters.csrf.CSRF
import ixias.model.Entity
import lib.model.ToDo
import monix.execution.misc.AsyncVar
import lib.model.Category
import cats.instances.long
import java.{util => ju}


@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController with play.api.i18n.I18nSupport{

  val todoInputForm = Form (
    mapping(
      "title" -> nonEmptyText,
      "body" -> text,
      "category" -> longNumber
    )(ToDoNew.apply)(ToDoNew.unapply)
  )

  val todoEditForm = Form (
    mapping(
      "title" -> nonEmptyText,
      "body" -> text,
      "category" -> longNumber,
      "state" -> shortNumber
    )
    ((title: String, body:String, category:Long, state:Short) => ToDoEdit.apply(title, body, category, ToDo.Status(state)))
    (ToDoEdit => Option(ToDoEdit.title, ToDoEdit.body, ToDoEdit.category, ToDoEdit.state.code))
  )

  val status = ToDo.Status.values.map(value => (value.code.toString(),value.name))

  def list() = Action.async {
    
    val todosFuture = ToDoRepository.getAll()
    
    val categoryFuture = CategoryRepository.getAll()
    
    for {
      todos <- todosFuture
      categories <- categoryFuture
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

  def editTodo(todoId : Long) = Action.async { implicit req =>
    implicit val token = CSRF.getToken(req).get
    
    val todoFuture = ToDoRepository.get(ToDo.Id(todoId))
    val categoryFuture = CategoryRepository.getAll().map(categories => categories.map(category => (category.id.toString(),category.v.name)))
    
    for {
      todo <- todoFuture
      categories <- categoryFuture
    }yield {
      val todoData = todo.get.v
      val todoMap = Map(
        "title" -> todoData.title,
        "body"  -> todoData.body,
        "category" -> todoData.categoryId.toString(),
        "state" -> todoData.state.code.toString()
      )
      val vv = ViewValueToDoEdit(
        title  = "Todo修正",
        cssSrc = Seq("main.css"),
        jsSrc  = Seq("main.js"),
        inputForm = todoEditForm.bind(todoMap),
        todoId
      )
      Ok(views.html.EditTodo(vv,categories,status))
    }
 
  }

  def editTodoSave(todoId : Long) = Action.async { implicit req =>
    implicit val token = CSRF.getToken(req).get
    todoEditForm.bindFromRequest().fold(
      formWithErrors => {
        for {
          categories <- CategoryRepository.getAll().map(categories => categories.map(category => (category.id.toString(),category.v.name)))
        }yield {
          BadRequest(views.html.EditTodo(ViewValueToDoEdit(
            title  = "Todo修正",
            cssSrc = Seq("main.css"),
            jsSrc  = Seq("main.js"),
            inputForm = formWithErrors,
            todoId
          ), categories, status))
        }
      },
      inputData => {
        for {
          newTodo <- ToDoRepository.update(ToDo(id=todoId, title = inputData.title, body = inputData.body, categoryId = inputData.category, state=inputData.state))
        } yield {
          Redirect(routes.TodoListController.list())
        }
      }
    )
  }

  def deleteTodo(todoId : Long) = Action.async { implicit req =>
    val deleteFuture = ToDoRepository.remove(ToDo.Id(todoId))
    for {
      deleteTodo <- deleteFuture
    }yield {
      Redirect(routes.TodoListController.list())
    }
  }

}
