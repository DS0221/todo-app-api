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

case class ToDoForm(
    categoryName:  Option[String],
    title:          String,
    body:           String,
    state:          String,
    categoryColor:  Option[String],
    id:             Long
)

@Singleton
class TodoListController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def list() = Action { implicit req =>

    val toDoListFuture = for {
      todos <- ToDoRepository.getAll()
      categories <- CategoryRepository.getAll()
    } yield {
      todos.map(
        todo =>
          ToDoForm(
            categories.find(_.id == todo.v.categoryId).map(_.v.name),
            todo.v.title,
            todo.v.body,
            todo.v.state.name,
            categories.find(_.id == todo.v.categoryId).map(_.v.color.name)
            ,todo.id
          )
      )
    }

    val toDoList = Await.ready(toDoListFuture, Duration.Inf).value.get.toEither.getOrElse(Seq.empty)

    val vv = ViewValueToDoList(
      title  = "Todo一覧",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js"),
      toDoList = toDoList
    )

    Ok(views.html.TodoList(vv))
  }

}
