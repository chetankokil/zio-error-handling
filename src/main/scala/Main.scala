import zio.*
import zio.stream.ZStream
// import zio.kafka.consumer.*
// import zio.kafka.producer.{ Producer, ProducerSettings }
// import zio.kafka.serde.*
// import org.apache.kafka.clients.producer.RecordMetadata
import http.Server
import cats.syntax.all.*
import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import doobie.hikari.*
import doobie.util.ExecutionContexts
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import zio.managed.ZManaged
import domain.repository.PersonRepository
import domain.repository.PersonRepositoryLive
import services.PersonService
import services.PersonServiceLive

object Main extends ZIOAppDefault {

  private val BOOSTRAP_SERVERS = List("localhost:29092")
  private val KAFKA_TOPIC      = "hello"

  val success = ZIO.succeed(42)

  // private def produce(topic: String, key: Long, value: String): RIO[Any with Producer, RecordMetadata] =
  //   Producer.produce[Any, Long, String](topic, key, value, keySerializer = Serde.long, valueSerializer = Serde.string)

  // private def producer: ZLayer[Any, Throwable, Producer] =
  //   ZLayer.scoped(Producer.make(ProducerSettings(BOOSTRAP_SERVERS)))

  //create a transactor defined in doobie
  private def transactor: ZManaged[Any, Throwable, Transactor[Task]] =
    HikariTransactor.newHikariTransactor[Task](
      "org.postgresql.Driver", 
      "jdbc:postgresql://localhost:5432/postgres",
      "postgres", 
      "postgres", 
      ExecutionContexts.synchronous
    ).toManagedZIO

  val environment = transactor.toLayer >>> PersonRepositoryLive.layer >>> PersonServiceLive.layer

  val app = for {
    - <- Server.run
  } yield ()

  def run = app.provideLayer(environment).exitCode
   
}
