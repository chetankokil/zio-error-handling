package http.routes

import sttp.model.StatusCode
import sttp.tapir.EndpointOutput.OneOf
import io.circe.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto.*
import zio.*
import zio.interop.catz.*
import org.http4s.HttpApp
import org.http4s.Request
import org.http4s.Response
import sttp.tapir.ztapir.*
import sttp.tapir.server.http4s.*
import sttp.tapir.server.http4s.ztapir.*
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import org.http4s.HttpRoutes
import http.ErrorInfo
import http.models.Person
import http.ErrorInfo.{ BadRequest, InternalServerError, NotFound }
import services.PersonService
import services.PersonServiceLive
import zio.kafka.producer.Producer

object Routes:
  val httpErrors: OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
    oneOfVariant(StatusCode.InternalServerError, jsonBody[InternalServerError]),
    oneOfVariant(StatusCode.BadRequest, jsonBody[BadRequest]),
    oneOfVariant(StatusCode.NotFound, jsonBody[NotFound])
  )

  val createPersonEndpoint: PublicEndpoint[Person, ErrorInfo, Long, Any] = endpoint.post
    .in("person")
    .in(jsonBody[Person])
    .out(plainBody[Long])
    .errorOut(httpErrors)

  val getPersonEndpoint = endpoint.get
    .in("person" / path[Long]("id"))
    .out(jsonBody[Option[Person]])
    .errorOut(httpErrors)

  val createPersonServer: ZServerEndpoint[PersonService & Producer, Any] = createPersonEndpoint.zServerLogic { person =>
    PersonService.create(person).mapError {
      case e: Exception =>
        println(e)
        InternalServerError("Internal Server Error")
      case _ => InternalServerError("Internal Server Error")
    }
  }

  val getPersonServer: ZServerEndpoint[PersonService & Producer, Any] = getPersonEndpoint.zServerLogic { (id: Long) =>
    PersonService.read(id).mapError(_ => InternalServerError(s"Person with Id $id not found"))
  }

  val helloWorldEndpoint: PublicEndpoint[Unit, ErrorInfo, String, Any] = endpoint.get
    .in("hello")
    .out(stringBody)
    .errorOut(httpErrors)

  val helloworldServer: ZServerEndpoint[PersonService & Producer, Any] = helloWorldEndpoint.zServerLogic { _ =>
    ZIO.succeed("Hello, World!")
  }

  val allEndpoints: List[ZServerEndpoint[PersonService & Producer, Any]] =
    List(createPersonServer, helloworldServer, getPersonServer)

  val routes = ZHttp4sServerInterpreter().from(allEndpoints).toRoutes
