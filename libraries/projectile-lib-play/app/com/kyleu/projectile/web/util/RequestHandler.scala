package com.kyleu.projectile.web.util

import com.kyleu.projectile.util.Logging
import com.kyleu.projectile.util.tracing.TraceData
import javax.inject.Inject
import play.api.http._
import play.api.mvc.RequestHeader
import play.api.routing.Router

class RequestHandler @Inject() (
    errorHandler: HttpErrorHandler,
    configuration: HttpConfiguration,
    filters: HttpFilters,
    router: Router
) extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) with Logging {

  override def routeRequest(request: RequestHeader) = {
    if (!Option(request.path).exists(x => x.startsWith("/assets") || x.startsWith("/style"))) {
      log.info(s"Request from [${request.remoteAddress}]: ${request.toString()}")(TraceData.noop)
    }
    super.routeRequest(request)
  }
}
