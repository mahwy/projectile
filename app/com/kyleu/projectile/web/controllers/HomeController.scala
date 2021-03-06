package com.kyleu.projectile.web.controllers

import better.files._
import play.api.mvc.Cookie
import com.kyleu.projectile.web.util.PlayServerHelper

import scala.concurrent.Future

@javax.inject.Singleton
class HomeController @javax.inject.Inject() () extends ProjectileController {
  def index = Action.async { implicit request =>
    Future.successful(Ok(com.kyleu.projectile.web.views.html.index(projectile, projectile.listInputs(), projectile.listProjects())))
  }

  private[this] val directoriesKey = "projectile-recent-directories"

  def changeDir(dir: Option[String]) = Action.async { implicit request =>
    dir match {
      case Some(d) =>
        val f = d.toFile
        if (f.isDirectory && f.isReadable) {
          val projectileDir = f / ".projectile"
          if (projectileDir.isDirectory && projectileDir.isReadable) {
            PlayServerHelper.setNewDirectory(d)
            val recent = request.cookies.get(directoriesKey).map(_.value.split("::").toSet).getOrElse(Set.empty)
            val newVal = (recent + d).toList.sorted.mkString("::")
            val c = request.cookies.get(directoriesKey).map(x => x.copy(value = newVal)).getOrElse(Cookie(directoriesKey, newVal))
            // Future.successful(Redirect(com.kyleu.projectile.web.controllers.routes.HomeController.index()).withCookies(c))
            val out = s"<html><head><title>Dir:${f.name}</title></head><body><p>Changed directory to [$d]</p></body></html>"
            Future.successful(Ok(play.twirl.api.Html(out)).withCookies(c))
          } else {
            Future.successful(Ok(com.kyleu.projectile.web.views.html.file.initDirForm(projectile, d)))
          }
        } else {
          val result = Redirect(com.kyleu.projectile.web.controllers.routes.HomeController.index())
          Future.successful(result.flashing("error" -> s"Directory [${f.pathAsString}] does not exist"))
        }

      case None =>
        val recent = request.cookies.get(directoriesKey).map(_.value.split("::").toList).getOrElse(Nil)
        Future.successful(Ok(com.kyleu.projectile.web.views.html.file.newDirForm(projectile, recent)))
    }
  }

  def initialize(d: String) = Action.async { implicit request =>
    PlayServerHelper.setNewDirectory(d)
    projectile.init()
    Future.successful(Redirect(com.kyleu.projectile.web.controllers.routes.HomeController.index()))
  }

  def testbed = Action.async { implicit request =>
    Future.successful(Ok(com.kyleu.projectile.web.views.html.testbed(projectile)))
  }

  def refreshAll = Action.async { implicit request =>
    Future.successful(Redirect(com.kyleu.projectile.web.controllers.routes.HomeController.index()).flashing {
      "success" -> "Refreshed a bunch of stuff; you're welcome"
    })
  }
}
