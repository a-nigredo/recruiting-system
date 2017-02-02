package com.levi9.domain.service.impl

import com.google.inject.name.{Named, Names}
import com.google.inject.{AbstractModule, Inject, Singleton}
import com.levi9.domain.service.{CandidateService, CommentService, UserService, VacancyService}
import net.codingwell.scalaguice.ScalaModule

class GuiceModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[CandidateService].annotatedWith(Names.named("DomainCandidateService")).to[CandidateServiceImpl].in[Singleton]
    bind[CommentService].to[CommentServiceImpl].in[Singleton]
    bind[VacancyService].annotatedWith(Names.named("DomainVacancyService")).to[VacancyServiceImpl].in[Singleton]
    bind[UserService].to[UserServiceImpl].in[Singleton]
  }
}

class CandidateServiceImpl @Inject()(@Named("SlickCommandCandidateDao") val dao: CandidateService.Dao,
                                     val validator: CandidateService#Validator) extends CandidateService

class CommentServiceImpl @Inject()(@Named("SlickCommandCommentDao") val dao: CommentService.Dao,
                                   val validator: CommentService#Validator) extends CommentService

class UserServiceImpl @Inject()(@Named("SlickCommandUserDao") val dao: UserService.Dao,
                                val validator: UserService#Validator) extends UserService

class VacancyServiceImpl @Inject()(@Named("SlickCommandVacancyDao") val dao: VacancyService.Dao,
                                   val validator: VacancyService#Validator) extends VacancyService