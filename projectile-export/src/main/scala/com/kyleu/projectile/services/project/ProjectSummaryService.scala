package com.kyleu.projectile.services.project

import io.scalaland.chimney.dsl._
import com.kyleu.projectile.models.project._
import com.kyleu.projectile.services.config.ConfigService
import com.kyleu.projectile.util.JacksonUtils
import com.kyleu.projectile.util.JsonSerializers._

class ProjectSummaryService(val cfg: ConfigService) {
  private[this] val dir = cfg.projectDirectory
  private[this] val fn = "project.json"

  def immediateList() = if (dir.exists) {
    dir.children.filter(_.isDirectory).toList.map(_.name.stripSuffix(".json")).flatMap(getSummary)
  } else {
    Nil
  }

  def list(): Seq[ProjectSummary] = immediateList() ++ cfg.linkedConfigs.map(c => new ProjectSummaryService(c)).flatMap(_.list()).sortBy(_.key)

  def getSummary(key: String): Option[ProjectSummary] = {
    val c = cfg.configForProject(key).getOrElse(throw new IllegalStateException(s"Cannot find project with key [$key]"))
    val d = c.projectDirectory
    val f = d / key / fn
    if (f.exists && f.isRegularFile && f.isReadable) {
      JacksonUtils.decodeJackson[ProjectSummary](f.contentAsString) match {
        case Right(is) => Some(is.copy(key = key))
        case Left(x) => throw x
      }
    } else {
      throw new IllegalStateException(s"Cannot load project with key [$key] from [${f.pathAsString}]")
    }
  }

  def add(p: ProjectSummary) = {
    val d = cfg.configForProject(p.key).getOrElse(cfg).projectDirectory
    val f = d / p.key / fn
    f.createFileIfNotExists(createParents = true)
    f.overwrite(JacksonUtils.printJackson(p.asJson))
    p.into[Project].transform
  }
}