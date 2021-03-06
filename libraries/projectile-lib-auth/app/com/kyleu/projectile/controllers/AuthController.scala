package com.kyleu.projectile.controllers

import com.kyleu.projectile.models.Application
import com.kyleu.projectile.models.auth.{AuthEnv, UserCredentials}
import com.kyleu.projectile.models.user.{Role, SystemUser}
import com.kyleu.projectile.util.metrics.Instrumented
import com.kyleu.projectile.util.tracing.TraceData
import com.mohiva.play.silhouette.api.actions.{SecuredRequest, UserAwareRequest}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

abstract class AuthController(name: String) extends BaseController(name) {
  type Req = SecuredRequest[AuthEnv, AnyContent]

  def app: Application

  override def tracing = app.tracing

  protected def withoutSession(action: String)(block: UserAwareRequest[AuthEnv, AnyContent] => TraceData => Future[Result])(implicit ec: ExecutionContext) = {
    app.silhouette.UserAwareAction.async { implicit request =>
      Instrumented.timeFuture(metricsName + "_request", "action", name + "_" + action) {
        app.tracing.trace(name + ".controller." + action) { td =>
          enhanceRequest(request, request.identity, td)
          block(request)(td)
        }(getTraceData)
      }
    }
  }

  protected def withSession(action: String, admin: Boolean = false)(block: Req => TraceData => Future[Result])(implicit ec: ExecutionContext) = {
    app.silhouette.UserAwareAction.async { implicit request =>
      request.identity match {
        case Some(u) => if (admin && u.role != Role.Admin) {
          failRequest(request)
        } else {
          Instrumented.timeFuture(metricsName + "_request", "action", name + "_" + action) {
            app.tracing.trace(name + ".controller." + action) { td =>
              enhanceRequest(request, Some(u), td)
              val auth = request.authenticator.getOrElse(throw new IllegalStateException("No auth!"))
              block(SecuredRequest(u, auth, request))(td)
            }(getTraceData)
          }
        }
        case None => failRequest(request)
      }
    }
  }

  protected implicit def toCredentials(request: SecuredRequest[AuthEnv, _]): UserCredentials = UserCredentials.fromRequest(request)

  protected def failRequest(request: UserAwareRequest[AuthEnv, AnyContent]) = {
    val msg = request.identity match {
      case Some(_) => "You must be an administrator to access that"
      case None => s"You must sign in or register before accessing ${app.projectName}"
    }
    Future.successful(Redirect(app.actions.failedRedirect).flashing("error" -> msg).withSession(request.session + ("returnUrl" -> request.uri)))
  }

  protected def enhanceRequest(request: Request[AnyContent], user: Option[SystemUser], trace: TraceData) = {
    super.enhanceRequest(request, trace)
    user.foreach { u =>
      trace.tag("user.id", u.id.toString)
      trace.tag("user.username", u.username)
      trace.tag("user.email", u.profile.providerKey)
      trace.tag("user.role", u.role.toString)
    }
  }
}
