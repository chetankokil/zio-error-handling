package domain.repository

import http.models.Person
import zio.*
import zio.interop.catz.*
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.transactor.Transactor
import doobie.util.transactor

trait PersonRepository extends Repository[Task, Person, Long]:
    def create(entity: Person): Task[Long]
    def read(id: Long): Task[Option[Person]]
    def update(entity: Person): Task[Unit]
    def delete(id: Long): Task[Unit]
    def list(): Task[List[Person]]


case class PersonRepositoryImpl(txn: Transactor[Task]) extends PersonRepository:
    def create(entity: Person): Task[Long] = 
        sql"""
            INSERT INTO person (name, age)
            VALUES (${entity.name}, ${entity.age})
        """.update
        .withUniqueGeneratedKeys[Long]("id")
        .transact(txn)
    def read(id: Long): Task[Option[Person]] = 
        sql"""
            SELECT id, name, age
            FROM person
            WHERE id = $id
        """.query[Person]
        .option
        .transact(txn)
    def update(entity: Person): Task[Unit] = 
        sql"""
            UPDATE person
            SET name = ${entity.name}, age = ${entity.age}
            WHERE id = ${entity.id}
        """.update.run.void.transact(txn)
    def delete(id: Long): Task[Unit] = 
        sql"""
            DELETE FROM person
            WHERE id = $id
        """.update.run.void.transact(txn)
    def list(): Task[List[Person]] = 
        sql"""
            SELECT id, name, age
            FROM person
        """.query[Person]
        .to[List]
        .transact(txn)

    object PersonRepositoryLive:
        val layer:ZLayer[Transactor[Task], Nothing, PersonRepositoryImpl] = 
            ZLayer.fromFunction(PersonRepositoryImpl(_))