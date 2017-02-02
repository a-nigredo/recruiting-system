package com.levi9.domain.model

import java.util.Date

import com.levi9.common.{SystemError, ValidationError}
import com.levi9.domain.model.Vacancies.Status.New

object Vacancies {

  type VacancyDomain = Vacancy
  type VacancyId = Id

  case class Vacancy(id: Id = Id(), description: String, requirements: String, project: String, seniority: Long,
                     position: Long, location: Int, quantity: Int, assignee: Long, owner: Long,
                     author: Long, modificationDate: Date = new Date(),
                     creationDate: Date = new Date(), status: StatusId = Status.to(New, New))
    extends Storable[VacancyId]

  object Vacancy {
    def apply(description: String, requirements: String, project: String, seniority: Long, position: Long,
              location: Int, quantity: Int, assignee: Long, owner: Long, author: Long): Vacancy =
      new Vacancy(description = description, requirements = requirements, project = project, seniority = seniority,
        position = position, location = location, quantity = quantity, assignee = assignee, owner = owner,
        author = author)
  }

  object Status {

    sealed trait Status {
      val id: Int
      val title: String
    }

    case object New extends Status {
      override val id = 1

      override val title = "New"

      override def toString = title
    }

    case object Approved extends Status {
      override val id = 2

      override val title = "Approved"

      override def toString = title
    }

    case object Open extends Status {
      override val id = 3

      override val title = "Open"

      override def toString = title
    }

    case object Offered extends Status {
      override val id = 4

      override val title = "Offered"

      override def toString = title
    }

    case object Closed extends Status {
      override val id = 5

      override val title = "Closed"

      override def toString = title
    }

    case object OnHold extends Status {
      override val id = 6

      override val title = "On hold"

      override def toString = title
    }

    private val all = List(New, Approved, Open, Offered, Closed, OnHold)

    @throws(classOf[ValidationError])
    def to(from: Status, to: Status): StatusId =
      if (from == to) StatusId(from.id)
      else from match {
        case New | Approved | Open | Offered | Closed => StatusId(to.id)
        case _ => throw ValidationError(s"Wrong status transition from '${from.title}' to '${to.title}'" :: Nil)
      }

    @throws(classOf[SystemError])
    def apply(id: Int) = find(id)

    @throws(classOf[SystemError])
    def apply(id: StatusId) = find(id.id)

    private def find(id: Int) = all.filter(_.id == id) match {
      case Nil => throw SystemError(new Exception(s"Invalid status id $id"))
      case x :: xs => x
    }

    def as[A](f: Status => A): List[A] = all.map(f)

    @throws(classOf[ValidationError])
    def next(status: Int): List[Status] = {
      val currentStatus = Status(status)
      (currentStatus :: (currentStatus match {
        case New | Approved | Open | Offered | Closed | OnHold => all
        case _ => throw ValidationError(s"Wrong status id $status" :: Nil)
      })).distinct
    }
  }

  case class StatusId(id: Int)

  object StatusId {

    implicit class StatusIdOps(statusId: StatusId) {
      def to(id: Int) = Status.to(Status(statusId.id), Status(id))
    }

  }

}
