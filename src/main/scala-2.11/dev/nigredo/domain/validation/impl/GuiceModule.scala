package com.levi9.domain.validation.impl

import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Inject}
import com.levi9.dao.Query._
import com.levi9.domain.model.Candidates.{CandidateDomain, Comment}
import com.levi9.domain.model.Users.User
import com.levi9.domain.model.Vacancies.VacancyDomain
import com.levi9.domain.validation.accord.{CandidateValidator, CommentValidator, UserValidator, VacancyValidator}
import com.levi9.domain.validation.privileges.CandidatePrivilegesValidator
import com.levi9.domain.validation.{Validator, ValidatorFacade}
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class GuiceModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    val candidateValidators = ScalaMultibinder.newSetBinder[Validator[CandidateDomain]](binder)
    val vacancyValidators = ScalaMultibinder.newSetBinder[Validator[VacancyDomain]](binder)
    candidateValidators.addBinding.to[CandidateValidatorImpl]
    candidateValidators.addBinding.to[CandidatePrivilegesValidatorImpl]
    vacancyValidators.addBinding.to[VacancyValidatorImpl]
    bind[Validator[CandidateDomain]].to[ValidatorFacadeImpl[CandidateDomain]]
    bind[Validator[Comment]].toInstance(new CommentValidator {})
    bind[Validator[VacancyDomain]].to[ValidatorFacadeImpl[VacancyDomain]]
    bind[Validator[User]].to[UserValidationImpl]
  }
}

private class CandidateValidatorImpl @Inject()(@Named("SlickQuerySeniorityDao") val seniorityDao: QuerySeniorityDao,
                                               @Named("SlickQueryPositionDao") val positionDao: QueryPositionDao,
                                               @Named("SlickQuerySourceTypeDao") val sourceTypeDao: QuerySourceTypeDao,
                                               @Named("SlickQueryVacancyDao") val vacancyDao: QueryVacancyDao,
                                               val configDao: QueryConfigDao)
  extends CandidateValidator

private class CandidatePrivilegesValidatorImpl @Inject()(@Named("SlickCommandCandidateDao") val candidateQueryDao: CandidatePrivilegesValidator#CandidateQueryDao,
                                                         @Named("SlickQueryUserDao") val userQueryDao: CandidatePrivilegesValidator#UserQueryDao)
  extends CandidatePrivilegesValidator

private class VacancyValidatorImpl @Inject()(@Named("SlickQuerySeniorityDao") val seniorityDao: QuerySeniorityDao,
                                             @Named("SlickQueryPositionDao") val positionDao: QueryPositionDao,
                                             @Named("SlickQueryUserDao") val userDao: QueryUserDao)
  extends VacancyValidator

private class ValidatorFacadeImpl[A] @Inject()(val validators: Set[Validator[A]])
  extends ValidatorFacade[A]

private class UserValidationImpl @Inject()(@Named("SlickQueryRoleDao") val queryRoleDao: QueryRoleDao)
  extends UserValidator