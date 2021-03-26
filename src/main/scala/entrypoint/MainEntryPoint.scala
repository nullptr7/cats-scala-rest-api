package com.github.nullptr7
package entrypoint

import repo.BookRepo
import repo.EmployeeRepo.EmployeeRepoImpl
import routes.{BookRoutes, EmployeeRoutes}

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.{Request, Response}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object MainEntryPoint extends IOApp {

  private[this] val bookRepoImpl    : BookRepo.BookRepoImpl = new BookRepo.BookRepoImpl
  private[this] val employeeRepoImpl: EmployeeRepoImpl      = new EmployeeRepoImpl()

  private[this] val commonRoutes: Kleisli[IO, Request[IO], Response[IO]] =
    Router[IO](
      "/" -> BookRoutes.routes(bookRepo = bookRepoImpl),
      "/" -> EmployeeRoutes.employeeRoutes(employeeRepoImpl)
      ).orNotFound

  override def run(args: List[String]): IO[ExitCode] = BlazeServerBuilder[IO](global).bindHttp(9000, "0.0.0.0")
                                                                                     .withHttpApp(commonRoutes)
                                                                                     .serve
                                                                                     .compile
                                                                                     .drain
                                                                                     .as(ExitCode.Success)
}
