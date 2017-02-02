package com.levi9.security

import scala.annotation.{StaticAnnotation, tailrec}
import scala.language.experimental.macros
import scala.reflect.api.Trees
import scala.reflect.macros.blackbox

class Privileges extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Privileges.impl
}

class CanChange(roles: String*) extends StaticAnnotation

class Name(name: String) extends StaticAnnotation

trait PrivilegesManager[T] {
  def canChange(role: String, from: T, to: T): List[Error]
}

trait PrivilegesView[T] {

  def apply(role: String): List[PrivilegesView.Privilege]
}

object PrivilegesView {

  case class Privilege(entity: String, fields: List[Field])

  case class Field(name: String, privileges: List[PrivilegeDef])

  case class PrivilegeDef(name: String, allow: Boolean)

}

case class Error(path: String)

case class Path[A](path: String, value: A, roles: List[String])

object Privileges {

  def impl(c: blackbox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    case class Path(roles: List[String], path: (List[String], String), privilegeName: String)

    def isPrimitive(clazz: String): Boolean = {
      val typeName = TypeName(clazz)
      (c.universe.definitions.StringClass.name == typeName
        || c.universe.definitions.ScalaPrimitiveValueClasses.seq.exists(_.name == typeName))
    }

    def pathsInModule(clazz: String, parent: String, module: List[Trees#Tree], privilegeName: String,
                      typeAliases: Seq[(String, String)], paths: List[String] = Nil,
                      roles: List[String] = Nil): List[Path] = {
      val clazzName = typeAliases.filter(_._1.equalsIgnoreCase(clazz)).collectFirst {
        case v => v._2
      }.getOrElse(clazz)
      if (module.isEmpty)
        List(Path(roles.map(_.filter(_.isLetterOrDigit)), ((parent :: paths).reverse, clazzName), privilegeName))
      else {
        module match {
          case q"${mods: Modifiers} class $className(..$fields) extends ..$parents { ..$body }" :: tail if mods.hasFlag(Flag.CASE) =>
            if (isPrimitive(clazzName)) {
              pathsInModule(clazz = clazzName, parent = parent, roles = roles, paths = paths, typeAliases = typeAliases,
                module = Nil, privilegeName = privilegeName)
            }
            else if (!className.toString.equalsIgnoreCase(clazzName)) {
              pathsInModule(clazz = clazzName, parent = parent, roles = roles, paths = paths, module = tail,
                typeAliases = typeAliases, privilegeName = privilegeName)
            } else {
              val result = fields flatMap {
                case (param: ValDef) if param.mods.hasFlag(Flag.CASEACCESSOR) => param.mods.annotations.flatMap {
                  case q"new CanChange(..${value})" =>
                    List(pathsInModule(clazz = param.tpt.toString, parent = param.name.toString,
                      roles = value.map(_.toString).toList, paths = parent :: paths, typeAliases = typeAliases,
                      module = tail, privilegeName = "change"))
                  case _ => Nil
                }
              }
              if (result.nonEmpty) result.flatten.toList
              else pathsInModule(clazz = clazzName, parent = parent, roles = roles, paths = paths, module = Nil,
                typeAliases = typeAliases, privilegeName = privilegeName)
            }
          case _ => pathsInModule(clazz = clazzName, parent = parent, roles = roles, paths = paths, module = module.tail,
            typeAliases = typeAliases, privilegeName = privilegeName)
        }
      }
    }

    def paths(className: String, parent: String, privilegeName: String,
              pathAcc: List[String] = Nil, roles: List[String] = Nil, stopTraverse: Boolean = false): List[Path] = {
      if (stopTraverse) List(Path(roles.map(_.filter(_.isLetterOrDigit)), ((parent :: pathAcc).reverse, className), privilegeName))
      else {
        val result = c.mirror.staticClass(className) match {
          case (clazz: ClassSymbol) if clazz.isCaseClass => clazz.typeSignature.decls.collect {
            case m: MethodSymbol if m.isPrimaryConstructor => m.typeSignature match {
              case method: MethodType if method.params.nonEmpty =>
                val result = (for {
                  params <- m.paramLists.head
                  annotation <- params.annotations.map(_.tree)
                } yield {
                  if (annotation.children.head.toString.equalsIgnoreCase("new CanChange")) {
                    val roles = annotation.children.tail.map {
                      case pq"$value" => value.toString
                    }
                    val typeSymbol = params.typeSignature.typeSymbol
                    paths(params.typeSignature.typeSymbol.name.toString, params.name.toString, "change", parent :: pathAcc, roles,
                      typeSymbol.isJava || typeSymbol.asClass.isPrimitive)
                  } else Nil
                }).flatten
                if (result.nonEmpty) result
                else paths(className, parent, privilegeName, pathAcc, roles, stopTraverse = true)
            }
          }.flatten.toList
        }
        result
      }
    }

    def materializePrivilegesManager(className: TypeName, privileges: List[Path]) = {
      @tailrec
      def buildPath(names: List[String], value: c.universe.Tree): c.universe.Tree =
        if (names.isEmpty) value
        else buildPath(names.tail, q"""$value.${TermName(names.head)}""")

      val from = privileges.map { x =>
        q"""com.levi9.security.Path[${TypeName(x.path._2)}](${x.path._1.mkString(".")},
               ${buildPath(x.path._1.tail, q"""from.${TermName(x.path._1.head)}""")}, ${x.roles})"""
      }
      val to = privileges.map { x =>
        q"""com.levi9.security.Path[${TypeName(x.path._2)}](${x.path._1.mkString(".")},
               ${buildPath(x.path._1.tail, q"""to.${TermName(x.path._1.head)}""")}, ${x.roles})"""
      }
      q"""implicit val privilegesManager: com.levi9.security.PrivilegesManager[$className] = new com.levi9.security.PrivilegesManager[$className] {
        override def canChange(role: String, from: $className, to: $className) = {
          List(..$from).zip(List(..$to))
            .filter(x => x._1.path == x._2.path && x._1.value != x._2.value && !x._1.roles.exists(_.equalsIgnoreCase(role)))
            .map(x => com.levi9.security.Error(x._1.path))
            }
      }"""
    }

    def materializePrivilegesView(className: TypeName, name: String, privileges: List[Path]) = {
      val fields = privileges.map(_.path._1.mkString(".")).distinct
      val privilegesName = privileges.map(_.privilegeName).distinct
      val allow = for {
        field <- fields
        privilegeName <- privilegesName
        roles <- privileges
          .filter(x => x.privilegeName.equalsIgnoreCase(privilegeName) && x.path._1.mkString(".").equalsIgnoreCase(field))
          .map(_.roles)
      } yield
        q"""com.levi9.security.PrivilegesView.Field($field, List(com.levi9.security.PrivilegesView.PrivilegeDef($privilegeName, $roles.exists(_.equalsIgnoreCase(role)))))"""

      q"""implicit val privilegesView: com.levi9.security.PrivilegesView[$className] = new com.levi9.security.PrivilegesView[$className] {
         override def apply(role: String) = List(com.levi9.security.PrivilegesView.Privilege($name, $allow))
         }"""
    }

    def findCompanionObject(caseClassName: String, module: Seq[Trees#Tree]): Seq[Trees#Tree] =
      module.filter {
        case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$objectBody }" =>
          tname.toString.equalsIgnoreCase(caseClassName)
        case _ => false
      }

    def findName(mods: Modifiers) = mods.annotations.collectFirst {
      case q"new com.levi9.security.Name($value)" => value.toString.filter(_.isLetterOrDigit)
      case q"new Name($value)" => value.toString.filter(_.isLetterOrDigit)
    }

    val result = annottees.map(_.tree).toList match {
      case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$objectBody }" :: tail =>
        val typeAliases = objectBody.filter {
          case q"$mods type $name[..$args] = $tpt" => true
          case _ => false
        } map {
          case q"$mods type $name[..$args] = $tpt" => (name.toString, tpt.toString)
        }

        val caseClassCompanionPair = objectBody filter {
          case q"${mods: Modifiers} class $tpname[..$tparams] $ctorMods(...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }" if mods.hasFlag(Flag.CASE) => true
          case _ => false
        } map {
          case caseClass@q"$mods class $className(..$fields) extends ..$parents { ..$body }" => (caseClass, findCompanionObject(className.toString, objectBody))
        }

        val body = objectBody filter (tree => !caseClassCompanionPair.exists(_._2.exists(_.equals(tree))))

        val companions = caseClassCompanionPair map {
          case (caseClass, companion) => caseClass match {
            case q"${mods: Modifiers} class $className(..$fields) extends ..$parents { ..$body }" =>
              val privileges = (fields map {
                case (param: ValDef) if param.mods.hasFlag(Flag.CASEACCESSOR) => param.mods.annotations.flatMap {
                  case q"new CanChange(..${value})" =>
                    pathsInModule(clazz = param.tpt.toString, parent = param.name.toString,
                      roles = value.map(_.toString).toList, module = objectBody.toList, privilegeName = "change",
                      typeAliases = typeAliases)
                  case _ => Nil
                }
              }).filter(_.nonEmpty).flatten.toList

              val result = if (privileges.nonEmpty) {
                val name = findName(mods).getOrElse(className.toString)
                val materializedPrivilegesView = materializePrivilegesView(TypeName(className.toString), name, privileges)
                val materializedPrivilegesManager = materializePrivilegesManager(TypeName(className.toString), privileges)
                companion map {
                  case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$objectBody }" =>
                    q"""$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$objectBody;$materializedPrivilegesView;$materializedPrivilegesManager;}"""
                } match {
                  case x :: xs => x
                  case Nil => q"""object ${TermName(className.toString)}{..$materializedPrivilegesManager;$materializedPrivilegesView}"""
                }
              } else companion match {
                case x :: xs => x match {
                  case q"$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$objectBody }" => q"""$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$objectBody;}"""
                }
                case _ => q""""""
              }

              c.Expr[Any](result)
          }
          case _ => c.Expr[Any](q"""""")
        }
        q"""$mods object $tname extends { ..$earlydefns } with ..$parents { $self => ..$body;..$companions;}"""
      case caseClass@q"${mods: Modifiers} class $className(..$fields) extends ..$parents { ..$body }" :: tail if mods.hasFlag(Flag.CASE) =>
        val privileges = fields map {
          case (param: ValDef) if param.mods.hasFlag(Flag.CASEACCESSOR) =>
            param.mods.annotations.flatMap {
              case q"new CanChange(..${value})" =>
                paths(param.tpt.toString, param.name.toString, "change", roles = value.map(_.toString).toList,
                  stopTraverse = isPrimitive(param.tpt.toString))
            }
        }
        val companion = if (privileges.nonEmpty) {
          val name = findName(mods).getOrElse(className.toString)
          val materializedPrivilegesView = materializePrivilegesView(TypeName(className.toString), name, privileges.flatten.toList)
          val materializedPrivilegesManager = materializePrivilegesManager(TypeName(className.toString), privileges.flatten.toList)
          tail match {
            case q"object $obj extends ..$bases { ..$body }" :: _ =>
              q"""object $obj extends ..$bases {..$body;$materializedPrivilegesManager;$materializedPrivilegesView}"""
            case _ => q"""object ${TermName(className.toString())}{$materializedPrivilegesManager;$materializedPrivilegesView}"""
          }
        } else tail match {
          case q"object $obj extends ..$bases { ..$body }" :: _ => q"""object $obj extends ..$bases {..$body;}"""
          case _ => q"""object ${TermName(className.toString())}{}"""
        }

        q"""${caseClass.head};$companion"""
      case _ => c.abort(c.enclosingPosition, "Annotation @Privileges can be used only with case classes or object")
    }
    c.Expr[Any](result)
  }
}