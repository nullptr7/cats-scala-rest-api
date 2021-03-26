package com.github.nullptr7
package routes

import model.BookModels.Message
import model.Employee
import repo.EmployeeRepo

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object EmployeeRoutes {

  def employeeRoutes(employeeRepo: EmployeeRepo[IO]): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case _@GET -> Root / "employees"    => employeeRepo.getAllEmployees.flatMap(Ok(_))
      case req@POST -> Root / "employees" => req.decode[Employee] { emp =>
        employeeRepo.addEmployee(emp).flatMap {
          case Left(errorMessage)      => NotFound(errorBody(errorMessage))
          case Right(insertedEmployee) => Ok(insertedEmployee)
        }
      }
    }
  }

  /** Default error message handler
   *
   * @param message given errorMessage
   * @return
   */
  private[this] def errorBody(message: Message): Json = Json.obj(("message", Json.fromString(message)))
}
