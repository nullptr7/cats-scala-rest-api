package com.github.nullptr7
package repo

import model.{ Hut, HutWithId }

import cats.effect._
import cats.implicits._

import java.util.UUID
import scala.collection.mutable.ListBuffer

case class HutRepository[F[_]](private val huts: ListBuffer[HutWithId])(implicit e: Sync[F]) {
  val maskedId: F[String] = e.delay(UUID.randomUUID().toString)

  def getHutById(id: String): F[Option[HutWithId]] =
    e.delay(huts.find(_.id == id))

  def addHut(hut: Hut): F[String] =
    for {
      uuid <- maskedId
      _ <- e.delay(huts += HutWithId(uuid, hut))
    } yield uuid

  def updateHut(hutWithId: HutWithId): F[Unit] =
    for {
      _ <- e.delay(huts -= hutWithId)
      _ <- e.delay(huts += hutWithId)
    } yield ()

  def deleteHut(hutWithId: String): F[Unit] =
    for {
      _ <- e.delay(huts.find(_.id == hutWithId).foreach(huts -= _))
    } yield ()
}

object HutRepository {
  def empty[F[_]](implicit f: Sync[F]): F[HutRepository[F]] = f.delay(new HutRepository[F](ListBuffer.empty[HutWithId]))
}
