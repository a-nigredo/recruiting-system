package com.levi9.dao.impl.slick

import com.google.inject.AbstractModule
import com.googlecode.flyway.core.Flyway
import com.levi9._
import com.levi9.dao.Command._
import com.levi9.dao.Migrator
import com.levi9.dao.Query._
import com.levi9.dao.impl.slick.command.{SlickAccessTokenDao, SlickCommentDao, SlickUserDao}
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.query._
import com.levi9.dao.impl.slick.query.filter._
import com.levi9.domain.model.Candidates.Comment
import net.codingwell.scalaguice.ScalaModule

class SlickModule extends AbstractModule with ScalaModule {

  def configure(): Unit = {
    val dataSource = new TypeSafeConfigMysqlDataSourceProvider("dataSource.mysql")
    val interpreter = new VocabularyInterpreter {
      override val profile = dataSource.profile
    }
    bind[JDBCDataSourceProvider].annotatedWithName("SlickJDBCDataSourceProvider").toInstance(dataSource)
    bind[QueryPositionDao].annotatedWithName("SlickQueryPositionDao").toInstance(new SlickPositionDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = interpreter
    })
    bind[QuerySeniorityDao].annotatedWithName("SlickQuerySeniorityDao").toInstance(new SlickSeniorityDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = interpreter
    })
    bind[QuerySourceTypeDao].annotatedWithName("SlickQuerySourceTypeDao").toInstance(new SlickSourceTypeDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = interpreter
    })
    bind[QueryRoleDao].annotatedWithName("SlickQueryRoleDao").toInstance(new SlickRoleDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = interpreter
    })

    val queryUserDao = new query.SlickUserDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = new UserInterpreter {
        override val profile = dataSource.profile
      }
    }
    bind[PageableQueryUserDao].annotatedWithName("SlickPageableQueryUserDao").toInstance(queryUserDao.pageableUserDao)
    bind[QueryUserDao].annotatedWithName("SlickQueryUserDao").toInstance(queryUserDao.userDao)
    val commandCandidateDao = new command.SlickCandidateDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
    }
    bind[CommandCandidateDao].annotatedWithName("SlickCommandCandidateDao").toInstance(commandCandidateDao)
    bind[CommandUserDao].annotatedWithName("SlickCommandUserDao").toInstance(new SlickUserDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
    })
    bind[CommandAccessTokenDao].annotatedWithName("SlickAccessTokenDao")
      .toInstance(new SlickAccessTokenDao with query.SlickAccessTokenDao {
        override val profile = dataSource.profile
        override val source = dataSource.source
        override val filterInterpreter = new AccessTokenInterpreter {
          override val profile = dataSource.profile
        }
      })
    bind[CommandCommentDao[Comment]].annotatedWithName("SlickCommandCommentDao").toInstance(new SlickCommentDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
    })
    val commandVacancyDao = new command.SlickVacancyDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
    }
    bind[CommandVacancyDao].annotatedWithName("SlickCommandVacancyDao").toInstance(commandVacancyDao)
    bind[QueryVacancyDao].annotatedWithName("SlickQueryVacancyDao").toInstance(new query.SlickVacancyDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = new VacancyInterpreter {
        override val profile = dataSource.profile
      }
    })
    bind[QueryCandidateDao].annotatedWithName("SlickQueryCandidateDao").toInstance(new query.SlickCandidateDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = new CandidateInterpreter {
        override val profile = dataSource.profile
      }
    })
    bind[QueryCommentDao].annotatedWithName("SlickQueryCommentDao").toInstance(new query.SlickCandidateCommentDao {
      override val profile = dataSource.profile
      override val source = dataSource.source
      override val filterInterpreter = new CandidateCommentInterpreter {
        override val profile = dataSource.profile
      }
    })
    val flyway = new Migrator {
      val fw = new Flyway
      fw.setDataSource(dataSourceConfig.getString("mysql.properties.url"),
        dataSourceConfig.getString("mysql.properties.user"),
        dataSourceConfig.getString("mysql.properties.password"))
      fw.setInitOnMigrate(true)

      override def migrate() = {
        fw.migrate()
      }
    }
    bind[Migrator].toInstance(flyway)
  }
}
