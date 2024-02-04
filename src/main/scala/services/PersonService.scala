package services

import http.models.Person
import domain.repository.PersonRepository
import zio.{ RIO, Task, URLayer, ZIO, ZLayer }
import domain.repository.PersonRepositoryLive
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import zio.kafka.producer.Producer
import zio.kafka.serde.Serde

trait PersonService:
  def create(entity: Person): Task[Long]
  def read(id: Long): Task[Option[Person]]
  def update(entity: Person): Task[Unit]
  def delete(id: Long): Task[Unit]
  def list(): Task[List[Person]]

object PersonService:
  def create(entity: Person): ZIO[PersonService & Producer, Throwable, Long] = for {
    l <- ZIO.serviceWithZIO[PersonService](_.create(entity))
    - <- ZIO.serviceWithZIO[Producer](_.produce("hello", 1L, entity.toString, Serde.long, Serde.string))
  } yield l
  def read(id: Long): ZIO[PersonService, Throwable, Option[Person]] = ZIO.serviceWithZIO[PersonService](_.read(id))
  def update(entity: Person): RIO[PersonService, Unit] = ZIO.serviceWithZIO[PersonService](_.update(entity))
  def delete(id: Long): RIO[PersonService, Unit]       = ZIO.serviceWithZIO[PersonService](_.delete(id))
  def list(): RIO[PersonService, List[Person]]         = ZIO.serviceWithZIO[PersonService](_.list());

case class PersonServiceLive(repo: PersonRepository) extends PersonService:
  def create(entity: Person): Task[Long]   = repo.create(entity)
  def read(id: Long): Task[Option[Person]] = repo.read(id)
  def update(entity: Person): Task[Unit]   = repo.update(entity)
  def delete(id: Long): Task[Unit]         = repo.delete(id)
  def list(): Task[List[Person]]           = repo.list()

object PersonServiceLive:
  val layer: URLayer[PersonRepository, PersonService] =
    ZLayer.fromFunction(PersonServiceLive(_))
