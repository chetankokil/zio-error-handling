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

  object Routes {
    val httpErrors: OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
      oneOfVariant(StatusCode.InternalServerError, jsonBody[InternalServerError]),
      oneOfVariant(StatusCode.BadRequest, jsonBody[BadRequest]),
      oneOfVariant(StatusCode.NotFound, jsonBody[NotFound])
    )


    val createPersonEndpoint: PublicEndpoint[Person, ErrorInfo, Person, Any] = endpoint.post
      .in("person")
      .in(jsonBody[Person])
      .out(jsonBody[Person])
      .errorOut(httpErrors)

    val createPersonServer: ZServerEndpoint[Any, Any] = createPersonEndpoint.zServerLogic[Any] { person =>
      ZIO.succeed(person)
    }

    val helloWorldEndpoint: PublicEndpoint[Unit, ErrorInfo, String, Any] = endpoint.get
      .in("hello")
      .out(stringBody)
      .errorOut(httpErrors)

    val helloworldServer: ZServerEndpoint[Any, Any] = helloWorldEndpoint.zServerLogic[Any] { _ =>
      ZIO.succeed("Hello, World!")
    }

    val allEndpoints = List(createPersonServer, helloworldServer)

    val endpoints: HttpRoutes[Task] =
      ZHttp4sServerInterpreter().from(allEndpoints).toRoutes
  }
