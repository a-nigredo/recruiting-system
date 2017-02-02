package com.levi9.service.impl

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Inject, Singleton}
import com.levi9.dao.Query.QueryConfigDao
import com.levi9.service.{AuthenticationService, AuthorizationService, CandidateService, VacancyService}
import net.codingwell.scalaguice.ScalaModule

class GuiceModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind[CandidateService].to[CandidateServiceImpl].in[Singleton]
    bind[VacancyService].to[VacancyServiceImpl].in[Singleton]
    bind[AuthorizationService].to[AuthorizationServiceImpl].in[Singleton]
    bind[AuthenticationService].to[AuthenticationServiceImpl].in[Singleton]
  }
}

private class AuthenticationServiceImpl @Inject()(@Named("SlickAccessTokenDao") val accessTokenDao: AuthenticationService#AccessTokenDao,
                                                  @Named("SlickCommandUserDao") val commandUserDao: AuthenticationService#CommandUserDao,
                                                  @Named("SlickQueryUserDao") val queryUserDao: AuthenticationService#QueryUserDao,
                                                  @Named("SlickQueryRoleDao") val roleDao: AuthenticationService#RoleDao,
                                                  val configDao: AuthenticationService#ConfigDao)
  extends AuthenticationService

private class AuthorizationServiceImpl @Inject()(val queryDao: AuthorizationService#ConfigDao)
  extends AuthorizationService

private class CandidateServiceImpl @Inject()(@Named("DomainCandidateService") val domainService: CandidateService#DomainService,
                                             @Named("SlickCommandCandidateDao") val dao: CandidateService#Dao,
                                             val configDao: QueryConfigDao)
  extends CandidateService

private class VacancyServiceImpl @Inject()(@Named("DomainVacancyService") val domainService: VacancyService#DomainService,
                                           @Named("SlickCommandVacancyDao") val dao: VacancyService#Dao)
  extends VacancyService