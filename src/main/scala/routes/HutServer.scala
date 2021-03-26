package com.github.nullptr7
package routes

import model._
import repo.HutRepository

import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._

import scala.concurrent.ExecutionContext.global

object HutServer extends IOApp with Http4sDsl[IO] {

  val HUTS = "huts"

  override def run(args: List[String]): IO[ExitCode] = {
    HutRepository.empty[IO].flatMap { x =>
      BlazeServerBuilder[IO](global).bindHttp(8080, "0.0.0.0")
                                    .withHttpApp(service[IO](x).orNotFound)
                                    .serve
                                    .compile
                                    .drain
                                    .as(ExitCode.Success)
    }
  }

  def service[F[_]](hutRepo: HutRepository[F])(implicit F: Sync[F]): HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / HUTS / hutId =>
      hutRepo.getHutById(hutId)
             .flatMap {
               case Some(hut) => Response[F](status = Status.Ok).withEntity(hut.asJson).pure[F]
               case None      => F.pure(Response(status = Status.NotFound))
             }

    case req@POST -> Root / HUTS =>
      req.decodeJson[Hut]
         .flatMap(hutRepo.addHut)
         .flatMap(hut => Response[F](status = Status.Created).withEntity(hut.asJson).pure[F])

    case req@PUT -> Root / HUTS =>
      req.decodeJson[HutWithId]
         .flatMap(hutRepo.updateHut)
         .flatMap(_ => F.pure(Response(status = Status.Ok)))

    case DELETE -> Root / HUTS / hutId =>
      hutRepo.deleteHut(hutId)
             .flatMap(_ => F.pure(Response(status = Status.NoContent)))
  }

}
