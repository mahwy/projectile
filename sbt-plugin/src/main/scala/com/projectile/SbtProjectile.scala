package com.projectile

import com.projectile.services.ProjectileService
import com.projectile.services.config.ConfigService
import sbt.Keys._
import sbt._
import complete.DefaultParsers.spaceDelimited

object SbtProjectile extends AutoPlugin {
  object autoImport {
    val projectile = inputKey[Unit]("Generate better code from your database, Thrift files, or GraphQL queries using Projectile")
  }

  override lazy val projectSettings = inConfig(Test)(projectileSettings) ++ inConfig(Compile)(projectileSettings)

  private[this] val projectileSettings: Seq[Setting[_]] = Seq(
    autoImport.projectile := {
      val streamValue = streams.value
      def log(s: String) = streamValue.log.info(s)
      val args: Seq[String] = spaceDelimited("<args>").parsed

      val svc = new ProjectileService(new ConfigService(baseDirectory.value.getPath))

      try {
        val startMs = System.currentTimeMillis
        val action = ProjectileCLI.parse(args)
        val result = action.map(act => svc.process(act.toCommand, act.verbose))
        result match {
          case Some(r) =>
            log(s"Code generation completed in [${System.currentTimeMillis - startMs}ms]")
            log(r.toString)
          case None =>
            log("No arguments")
        }
      } catch {
        case x: Throwable =>
          // x.printStackTrace()
          log(s"Error running [${args.mkString(" ")}]: $x")
      }
    }
  )
}
