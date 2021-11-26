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
import json.writes.JsValueTodoList
import json.reads.JsValueCreateTodo
import json.writes.JsValueDeleteTodo
import play.api.libs.json.Json
import json.writes.JsValueSelectTodo
import json.reads.JsValueUpdateTodo
import json.writes.JsValueTodoStatusList


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
      val jsValue = JsValueTodoList.apply(todoList)
      Ok(Json.toJson(jsValue))
      // val vv = ViewValueToDoList(
      //   title  = "Todo一覧",
      //   cssSrc = Seq("main.css"),
      //   jsSrc  = Seq("main.js"),
      //   toDoList = todoList
      // )
      // Ok(views.html.TodoList(vv))
    }
  }

  def statusList() = Action {
    val todoStatus = ToDo.Status.values
    val jsValue = JsValueTodoStatusList.apply(todoStatus)
    Ok(Json.toJson(jsValue))
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

  def newTodoSave() = Action(parse.json).async { implicit req =>

    req.body.validate[JsValueCreateTodo].fold(
      errors => {
        Future.successful{
          BadRequest("error")
        }
      },
      todoData => {
        for {
           newTodo <- ToDoRepository.add(ToDo(title = todoData.title, body = todoData.body, categoryId = todoData.category))
         } yield {
           val jsValue = JsValueCreateTodo.apply(todoData.title, todoData.body, todoData.category)
           Ok(Json.toJson(jsValue))
         }
      }
    )
  }

  def editTodo(todoId : Long) = Action.async { implicit req =>
    implicit val token = CSRF.getToken(req).get
    
    val todoFuture = ToDoRepository.get(ToDo.Id(todoId))
    //val categoryFuture = CategoryRepository.getAll().map(categories => categories.map(category => (category.id.toString(),category.v.name)))
    
    for {
      todo <- todoFuture
      //categories <- categoryFuture
    }yield {
      val todoData = todo.get.v
      val todoEdit = ToDoEdit.apply(
        todoData.title,
        todoData.body,
        todoData.categoryId,
        todoData.state
      )
      
      val jsValue = JsValueSelectTodo.apply(todoEdit)
      Ok(Json.toJson(jsValue))

    }
 
  }

  def editTodoSave() = Action(parse.json).async { implicit req =>
    req.body.validate[JsValueUpdateTodo].fold(
      errors => {
        Future.successful{
          BadRequest("error")
        }
      },
      inputData => {
        for {
           newTodo <- ToDoRepository.update(ToDo(id=inputData.id, title = inputData.title, body = inputData.body, categoryId = inputData.category, state=ToDo.Status(inputData.state)))
         } yield {
           val jsValue = JsValueUpdateTodo.apply(inputData.title, inputData.body, inputData.category, inputData.state, inputData.id)
           Ok(Json.toJson(jsValue))
         }
      }
    )

  }

  def deleteTodo(todoId : Long) = Action.async { implicit req =>
    val deleteFuture = ToDoRepository.remove(ToDo.Id(todoId))
    for {
      deleteTodo <- deleteFuture
    }yield {
      val jsValue = JsValueDeleteTodo.apply(deleteTodo.get.id)
      Ok(Json.toJson(jsValue))
    }
  }

}
