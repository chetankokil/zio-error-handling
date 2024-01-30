package http

import http.routes.Routes
import cats.syntax.all.*
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.*
import zio.*
import org.http4s.server.Router
import sttp.tapir.ztapir.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import org.http4s.blaze.server.BlazeServerBuilder
import scala.concurrent.ExecutionContext

object Server:

  def run : Task[Any] = 
    ZIO.executor.flatMap( executor => 
            BlazeServerBuilder[Task](executor.asExecutionContext)
           .bindHttp(8080, "localhost")
           .withHttpApp(Router("/" -> Routes.endpoints).orNotFound)
           .serve
           .compile
           .drain)
