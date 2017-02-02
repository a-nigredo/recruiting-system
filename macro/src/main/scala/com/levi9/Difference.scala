package com.levi9

import scala.annotation.Annotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

case class Difference(entity: String, field: String, oldValue: String, newValue: String, parent: Option[String] = None)

object Difference {

  class ByField(field: String) extends Annotation

  class Ignore(fields: String*) extends Annotation

  class BasicTypeAsName extends Annotation

  def apply[A <: Product](currentState: A, newState: A): List[Difference] = macro DifferenceImpl.impl[A]
}

object DifferenceImpl {

  def impl[A: c.WeakTypeTag](c: blackbox.Context)(currentState: c.Expr[A], newState: c.Expr[A]): c.Expr[List[Difference]] = {
    import c.universe._

    def mk(tpe: c.universe.Type, path: List[Symbol] = Nil, parent: Option[String] = None): c.universe.Tree = {

      def buildPath(path: List[Symbol], value: c.universe.Tree): c.universe.Tree =
        if (path.isEmpty) value
        else {
          val name = path.head.name.toString
          if (path.head.typeSignature.<:<(typeOf[Option[_]]))
            q"""$value.${TermName(name)}.map { _ =>
              ${buildPath(path.tail, q"""$value.${TermName(name)}.${TermName("get")}""")}
          }.getOrElse(${buildPath(Nil, q"""$value.${TermName(name)}""")})"""
          else buildPath(path.tail, q"""$value.${TermName(name)}""")
        }

      val allFields = if (tpe.typeSymbol.asClass.isTrait) tpe.decls.collect {
        case x: TermSymbol if x.isMethod => x
      }.toList
      else tpe.decls.collectFirst {
        case m: MethodSymbol if m.isPrimaryConstructor => m
      }.get.paramLists.head

      def isIgnored(symbol: Symbol) = {
        tpe.typeSymbol.annotations.map(_.tree).filter(_.tpe.<:<(typeOf[Difference.Ignore])).exists { x =>
          val values = x.children.tail
          if (values.isEmpty)
            c.abort(c.enclosingPosition, s"@Ignore can't be empty. Add fields or remove annotation. See @Ignore for '${tpe.typeSymbol.fullName.toString}'")
          else
            values.exists {
              case Literal(Constant(value: String)) =>
                if (!allFields.exists(_.name.toString.equalsIgnoreCase(value)))
                  c.abort(c.enclosingPosition, s"Can't find field '$value' in class '${tpe.typeSymbol.fullName.toString}'. See @Ignore for '${tpe.typeSymbol.fullName.toString}'")
                else value.equalsIgnoreCase(symbol.name.toString)
            }
        }
      }

      val filteredFields = allFields.filter(!isIgnored(_))

      def run(fieldPredicate: Symbol => Boolean,
              mkDiffPredicate: (Tree, Tree) => c.Expr[Boolean] = (v1, v2) => c.Expr[Boolean](q"""$v1 == $v2""")) = {

        def process(field: Symbol) = {

          def mkDiff(v1: List[Symbol], v2: List[Symbol], clazz: String, fieldName: String) = {
            val path1 = buildPath(v1, currentState.tree)
            val path2 = buildPath(v2, newState.tree)

            val pathToCaseClass1 = buildPath(v1.take(v1.size - 1), currentState.tree)

            val entity =
              if (tpe.typeSymbol.annotations.exists(_.tree.tpe.<:<(typeOf[Difference.BasicTypeAsName])))
                q"""$clazz"""
              else
                q"""$pathToCaseClass1.getClass.getSimpleName"""

            q"""if(${mkDiffPredicate(path1, path2)}) None else {
              val (v1, v2) = ($path1: Any, $path2: Any) match {
                case (None, _) => ("", $path2)
                case (_, None) => ($path1, "")
                case (_, _) => ($path1, $path2)
              }
              Some(Difference($entity, $fieldName, v1.toString, v2.toString, $parent))
            }"""
          }

          val finalPath = path :+ field

          q"""List(${
            mkDiff(finalPath, finalPath, tpe.typeSymbol.name.toString, field.name.toString)
          })"""
        }

        q"""${
          filteredFields.filter { field =>
            if (tpe.decl(field.name).typeSignature.resultType.<:<(typeOf[List[_]])) false
            else fieldPredicate(field)
          }.map { field =>
            val resultType = tpe.decl(field.name).typeSignature.finalResultType
            if (resultType.<:<(typeOf[Option[_]])) {
              val t = resultType.typeArgs.head
              if (t.<:<(typeOf[Product]) || t.typeSymbol.asClass.isTrait)
                q"""${mk(t, path :+ field, Some(tpe.typeSymbol.name.toString))}"""
              else process(field)
            } else {
              if (resultType.<:<(typeOf[Product]) || resultType.typeSymbol.asClass.isTrait)
                q"""${mk(resultType, path :+ field, Some(tpe.typeSymbol.name.toString))}"""
              else process(field)
            }
          }
        }.flatten"""
      }

      (tpe.typeSymbol.annotations.map(_.tree).filter(_.tpe.<:<(typeOf[Difference.ByField])) match {
        case x :: xs =>
          x.children.tail match {
            case List(Literal(Constant(value: String))) =>
              if (filteredFields.exists(_.name.toString.equalsIgnoreCase(value))) Some(value)
              else c.abort(c.enclosingPosition, s"Can't find field '$value' in '${tpe.typeSymbol.fullName}'. Please check param for @ByField")
            case _ => None
          }
        case _ => None
      }).map { x =>
        val fSymbol = filteredFields.filter(_.name.toString.equalsIgnoreCase(x)).head
        q"""if(${buildPath(path :+ fSymbol, currentState.tree)} != ${buildPath(path :+ fSymbol, newState.tree)})
                  ${run(!_.name.toString.equalsIgnoreCase(x), (_, _) => c.Expr[Boolean](q"""false"""))} else Nil"""
      }.getOrElse(run(_ => true))
    }

    c.Expr[List[Difference]](q"""${mk(weakTypeOf[A])}.filter(_.isDefined).map(_.get)""")
  }

}
