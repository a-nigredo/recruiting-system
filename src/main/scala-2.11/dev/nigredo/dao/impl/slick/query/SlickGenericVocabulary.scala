package com.levi9.dao.impl.slick.query

import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.VocabularyTable
import com.levi9.dao.impl.slick.query.filter.BasicInterpreter

import scala.concurrent.ExecutionContext.Implicits.global

protected[query] trait SlickGenericVocabulary[A]
  extends JDBCDataSourceProvider
    with VocabularyTable {

  import profile.api._

  val filterInterpreter: BasicInterpreter[VocabularyTable#Vocabularies]

  type TableQueryType = TableQuery[Vocabularies]

  protected def one(filter: Filter, runWith: TableQueryType) = for {
    result <- source.run(runWith.filter(x => filterInterpreter.interpret(filter, x)).result.headOption)
  } yield result

  protected def all(filter: Option[Filter], runWith: TableQueryType) = {
    val query = filter.map(f => runWith.filter(x => filterInterpreter.interpret(f, x))).getOrElse(runWith)
    for {
      result <- source.run(query.result)
    } yield result.seq.toList
  }
}
