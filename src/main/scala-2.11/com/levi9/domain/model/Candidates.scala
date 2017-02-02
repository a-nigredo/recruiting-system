package com.levi9.domain.model

import java.util.Date

import com.levi9.common._
import com.levi9.domain.model.Candidates.Status.OnHold
import com.levi9.security.{CanChange, Name, Privileges}

@Privileges
object Candidates {

  type CandidateDomain = Candidate
  type CandidateComment = Comment
  type CandidateCv = Cv

  @Name("candidate")
  case class Candidate(id: Id = Id(), name: String, surname: String, email: String, phone: String,
                       skype: String, seniority: Long, position: Long, source: Long, author: Long,
                       cv: Option[Cv] = None, @CanChange("hr", "admin") status: StatusId = Status.to(OnHold, OnHold),
                       modificationDate: Date = new Date(), creationDate: Date = new Date(),
                       location: Int, vacancy: Option[Id] = None) extends Storable[Id]

  object Candidate {
    def apply(name: String, surname: String, email: String, phone: String, skype: String, seniority: Long,
              position: Long, source: Long, author: Long, location: Int): Candidate =
      new Candidate(name = name, surname = surname, email = email, phone = phone, skype = skype, seniority = seniority,
        position = position, source = source, author = author, location = location)
  }

  sealed trait Cv extends Storable[Id] {
    val id: Id
    val fileName: String
    val candidate: Id
    val creationDate: Date
    val content: String
    def extension = this.fileName.substring(this.fileName.lastIndexOf(".") + 1)
  }

  case class NewCv(id: Id = Id(), fileName: String, candidate: Id, creationDate: Date = new Date(), content: String) extends Cv
  case class ExistingCv(id: Id = Id(), fileName: String, candidate: Id, creationDate: Date = new Date(), content: String) extends Cv

  case class Comment(id: Id = Id(), body: String, isPrivate: Boolean, creationDate: Date = new Date(),
                     author: Long, candidate: Id) extends Storable[Id]

  object Status {

    sealed trait Status {
      val id: Int
      val title: String
    }

    case object OnHold extends Status {
      override val id = 1

      override val title = "On hold"

      override def toString = title
    }

    case object NotAFit extends Status {
      override val id = 2

      override val title = "Not a Fit"

      override def toString = title
    }

    case object Offered extends Status {
      override val id = 3

      override val title = "Offered"

      override def toString = title
    }

    case object Accepted extends Status {
      override val id = 4

      override val title = "Accepted"

      override def toString = title
    }

    case object Rejected extends Status {
      override val id = 5

      override val title = "Rejected"

      override def toString = title
    }

    case object Screening extends Status {
      override val id = 6

      override val title = "Screening"

      override def toString = title
    }

    case object Interview extends Status {
      override val id = 7

      override val title = "Interview"

      override def toString = title
    }

    private val all = List(OnHold, NotAFit, Offered, Accepted, Rejected, Screening, Interview)

    @throws(classOf[ValidationError])
    def to(from: Status, to: Status): StatusId =
      if (from == to) StatusId(from.id)
      else from match {
        case OnHold if to == Screening => StatusId(to.id)
        case Screening if to == Interview || to == OnHold => StatusId(to.id)
        case Interview if to == Offered || to == NotAFit => StatusId(to.id)
        case Offered if to == Accepted || to == Rejected => StatusId(to.id)
        case NotAFit | Accepted | Rejected if to == OnHold => StatusId(to.id)
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
      currentStatus :: (currentStatus match {
        case Screening => List(OnHold, Interview)
        case Interview => List(Offered, NotAFit)
        case Offered => List(Accepted, Rejected)
        case Accepted | Rejected | NotAFit => List(OnHold)
        case OnHold => Nil
        case _ => throw ValidationError(s"Wrong status id $status" :: Nil)
      })
    }
  }

  case class StatusId(id: Int)

  object StatusId {

    implicit class StatusIdOps(statusId: StatusId) {
      def to(id: Int) = Status.to(Status(statusId.id), Status(id))
    }

  }

}