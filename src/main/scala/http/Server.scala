package http

import org.http4s.*
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.generic.auto.*
import sttp.tapir.server.http4s.ztapir.*
import sttp.tapir.ztapir.*
import zio.interop.catz.*
import zio.*
import scala.concurrent.ExecutionContext
import cats.syntax.all.*
import http.routes.Routes

object Server:
  def run = 
    ZIO.executor.flatMap( executor => 
          BlazeServerBuilder(executor.asExecutionContext)
           .bindHttp(8080, "localhost")
           .withHttpApp(Router("/" -> Routes.routes).orNotFound)
           .serve
           .compile
           .drain)
