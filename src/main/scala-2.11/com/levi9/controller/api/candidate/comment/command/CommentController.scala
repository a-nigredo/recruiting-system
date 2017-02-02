package com.levi9.controller.api.candidate.comment.command

import akka.http.scaladsl.server.Directives._
import com.levi9.controller.Controller
import com.levi9.domain.model.Candidates.Comment
import com.levi9.domain.model.Id
import com.levi9.domain.service.CommentService
import com.levi9.projection.User
import com.levi9.representation.json.Query._

trait CommentController {
  this: Controller =>

  type CommentServiceType = CommentService

  val commentService: CommentServiceType

  case class Payload(body: String, isPrivate: Boolean)

  implicit val payloadFormat = jsonFormat2(Payload)

  def comment(candidateId: String, user: User) = {

    def create(candidateId: String, user: User) = (post & entity(as[Payload]) & pathEnd) { dto =>
      val comment = Comment(body = dto.body, isPrivate = dto.isPrivate, candidate = Id(candidateId), author = user.id)
      respond(commentService.save(comment)())(x => Created(x.id.toJson.toString))
    }

    pathPrefix("comments")(create(candidateId, user))
  }
}