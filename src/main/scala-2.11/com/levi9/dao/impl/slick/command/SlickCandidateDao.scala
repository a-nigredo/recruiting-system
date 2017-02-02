package com.levi9.dao.impl.slick.command

import com.levi9.dao.Command.CandidateDao
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.{CandidateTable, CvTable}
import com.levi9.domain.model.Candidates._
import com.levi9.domain.model.Id

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._

protected[slick] trait SlickCandidateDao
  extends CandidateDao
    with CandidateTable
    with CvTable
    with JDBCDataSourceProvider {

  import profile.api._

  override def save(entity: CandidateDomain) = {
    def cvQuery(cv: Option[Cv]) = entity.cv match {
      case Some(value@NewCv(_, _, _, _, _)) => cv.map(x => (cvs += value).map(Some(_))).getOrElse(DBIO.successful(None))
      case _ => DBIO.successful(())
    }

    val query = for {
      _ <- candidates += entity.copy(creationDate = entity.modificationDate)
      _ <- cvQuery(entity.cv)
      _ <- candidatesSnapshot.insertOrUpdate(entity)
    } yield ()
    source.run(query.transactionally)
  }

  override def findOne(id: Id) = {
    source.run(candidatesSnapshot.joinLeft(cvs).on(_.cv === _.id).filter(_._1.id === id).result.headOption).mapF { case (row, cv) =>
      Candidate(id = row.id, name = row.name, surname = row.surname, email = row.email, phone = row.phone,
        skype = row.skype, seniority = row.seniority, position = row.position, source = row.source,
        status = Status.to(Status(row.status), Status(row.status)), creationDate = row.creationDate,
        author = row.author, vacancy = row.vacancy.map(Id(_)), location = row.location,
        cv = cv.map(x => ExistingCv(x.id, x.name, x.candidate, x.creationDate, x.content)))
    }
  }

  implicit def CandidateSnapshotDomain2Row(entity: CandidateDomain): CandidateSnapshotRow =
    CandidateSnapshotRow(id = entity.id, name = entity.name,
      surname = entity.surname, email = entity.email, phone = entity.phone,
      skype = entity.skype, seniority = entity.seniority, position = entity.position,
      source = entity.source, creationDate = entity.creationDate, status = entity.status.id,
      author = entity.author, vacancy = entity.vacancy.map(_.value), location = entity.location,
      cv = entity.cv.map(_.id))

  implicit def CandidateDomain2Row(entity: CandidateDomain): CandidateRow =
    CandidateRow(id = entity.id, name = entity.name,
      surname = entity.surname, email = entity.email, phone = entity.phone,
      skype = entity.skype, seniority = entity.seniority, position = entity.position,
      source = entity.source, creationDate = entity.creationDate, status = entity.status.id,
      author = entity.author, vacancy = entity.vacancy.map(_.value), location = entity.location)

  implicit def CvDomain2Row(entity: Cv): CvRow = CvRow(id = entity.id, name = entity.fileName,
    candidate = entity.candidate, creationDate = entity.creationDate, content = entity.content)
}