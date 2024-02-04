package services

import http.models.Person
import domain.repository.PersonRepository
import zio.{ Task, URLayer, ZIO, ZLayer }
import domain.repository.PersonRepositoryLive
import zio.interop.catz.*
import zio.interop.catz.implicits.*

trait PersonService:
  def create(entity: Person): Task[Long]
  def read(id: Long): Task[Option[Person]]
  def update(entity: Person): Task[Unit]
  def delete(id: Long): Task[Unit]
  def list(): Task[List[Person]]

object PersonService:
  def create(entity: Person): ZIO[PersonService, Throwable, Long] = ZIO.serviceWithZIO[PersonService](_.create(entity))
  def read(id: Long): ZIO[PersonService, Throwable, Option[Person]] = ZIO.serviceWithZIO[PersonService](_.read(id))
  def update(entity: Person): Task[Unit]                            = ???
  def delete(id: Long): Task[Unit]                                  = ???
  def list(): Task[List[Person]]                                    = ???

case class PersonServiceLive(repo: PersonRepository) extends PersonService:
  def create(entity: Person): Task[Long]   = repo.create(entity)
  def read(id: Long): Task[Option[Person]] = repo.read(id)
  def update(entity: Person): Task[Unit]   = repo.update(entity)
  def delete(id: Long): Task[Unit]         = repo.delete(id)
  def list(): Task[List[Person]]           = repo.list()

object PersonServiceLive:
  val layer: URLayer[PersonRepository, PersonService] =
    ZLayer.fromFunction(PersonServiceLive(_))

