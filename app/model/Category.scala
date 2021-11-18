package model

import play.api.data.Form

// categoryのviewvalue
case class ViewValueCategoryList(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  categoryList: Seq[CategoryForm]
) extends ViewValueCommon

// categoryの新規作成時viewvalue
case class ViewValueCategoryNew(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  inputForm: Form[CategoryNew]
) extends ViewValueCommon

case class ViewValueCategoryEdit(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  inputForm: Form[CategoryEdit],
  categoryId : Long
) extends ViewValueCommon

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

case class CategoryEdit(
  name:      String,
  slug:      String,
  color:     Short
)
