package com.levi9.dao.impl.slick.command

import com.levi9.dao.Command.VacancyDao
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.VacancyTable
import com.levi9.domain.model.Id
import com.levi9.domain.model.Vacancies.{Vacancy, VacancyDomain}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._

protected[slick] trait SlickVacancyDao
  extends VacancyDao
    with VacancyTable
    with JDBCDataSourceProvider {

  import profile.api._

  override def save(entity: VacancyDomain) = {
    val query = for {
      _ <- vacancies += entity.copy(creationDate = entity.modificationDate)
      _ <- vacanciesSnapshot.insertOrUpdate(entity)
    } yield ()
    source.run(query.transactionally)
  }

  implicit def Entity2Row(entity: VacancyDomain): VacancyRow =
    VacancyRow(entity.id, entity.project, entity.description, entity.requirements, entity.seniority, entity.position,
      entity.location, entity.quantity, entity.assignee, entity.owner, entity.creationDate, entity.author, entity.status.id)

  override def findOne(id: Id) = {
    val q = vacanciesSnapshot.filter(_.id === id.value)

    source.run(q.result.headOption).mapF(row =>
        Vacancy(Id(row.id), row.description, row.requirements, row.project, row.seniority, row.position,
          row.location, row.quantity, row.assignee, row.owner, author = row.author, creationDate = row.creationDate)
    )
  }
}