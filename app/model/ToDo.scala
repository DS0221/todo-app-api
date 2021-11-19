package model

import play.api.data.Form
import lib.model.ToDo

// todoの一覧出力時viewvalue
case class ViewValueToDoList(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  toDoList: Seq[ToDoForm]
) extends ViewValueCommon

// todoの新規作成時viewvalue
case class ViewValueToDoNew(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  inputForm: Form[ToDoNew]
) extends ViewValueCommon

// todoの修正時viewvalue
case class ViewValueToDoEdit(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  inputForm: Form[ToDoEdit],
  todoId : Long
) extends ViewValueCommon

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

case class ToDoEdit(
  title:      String,
  body:       String,
  category:   Long,
  state:      ToDo.Status
)
