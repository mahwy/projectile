package com.kyleu.projectile.web.util

import play.api.libs.ws._
import com.kyleu.projectile.util.tracing.{OpenTracingService, TraceData}

import scala.concurrent.ExecutionContext

class TracingWSClient @javax.inject.Inject() (val ws: WSClient, tracer: OpenTracingService) {
  def underlying[T]: T = ws.underlying
  def url(spanName: String, url: String)(implicit traceData: TraceData, ctx: ExecutionContext): WSRequest = {
    new TracingWSRequest(spanName, ws.url(url), tracer, traceData)
  }
  def close(): Unit = ws.close()
}
