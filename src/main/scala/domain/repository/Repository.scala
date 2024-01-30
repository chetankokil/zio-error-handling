package domain.repository

trait Repository[F[_], ENTITY, ID]:
  def create(entity: ENTITY): F[ID]
  def read(id: ID): F[Option[ENTITY]]
  def update(entity: ENTITY): F[Unit]
  def delete(id: ID): F[Unit]
  def list(): F[List[ENTITY]]