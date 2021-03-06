import sbt._
import sbt.Keys._

object ProjectileExport {
  lazy val `projectile-export` = (project in file("projectile-export")).settings(Common.settings: _*).settings(
    description := "Project configuration, export, and code generation from Projectile",
    libraryDependencies ++= Seq(Dependencies.Utils.clist, Dependencies.Utils.clistMacros, Dependencies.Testing.scalaTest),
  ).dependsOn(
    ParserProjects.`projectile-parser-database`,
    ParserProjects.`projectile-parser-graphql`,
    ParserProjects.`projectile-parser-thrift`,
    ParserProjects.`projectile-parser-typescript`
  ).disablePlugins(sbtassembly.AssemblyPlugin)
}
