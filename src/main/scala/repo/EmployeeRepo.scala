package com.github.nullptr7
package repo

import model.BookModels.Message
import model.{Address, Employee}

import cats.effect._
import cats.implicits._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

sealed trait EmployeeRepo[F[_]] {
  def getAllEmployees: F[List[Employee]]

  def addEmployee(employee: Employee): F[Either[Message, Employee]]
}

object EmployeeRepo {

  private[this] val listOfEmployees          : ListBuffer[Employee]           = ListBuffer.empty[Employee]
  private[this] val listOfAddressOfAnEmployee: mutable.HashMap[Long, Address] = mutable.HashMap.empty[Long, Address]

  private[this] def tryInsertAddress(employeeEither: Either[Message, Employee])(implicit e: Sync[IO]): IO[Unit] = {
    if (employeeEither.isRight && employeeEither.right.get.address.isDefined)
      listOfAddressOfAnEmployee.put(employeeEither.right.get.id, employeeEither.right.get.address.get)
    e.delay((): Unit)
  }

  private[this] def tryInsertEmployee(employeeOption: Option[Employee], empToInsert: Employee)
                                     (implicit e: Sync[IO]): IO[Either[Message, Employee]] =
    employeeOption match {
      case Some(_) =>
        e.delay(Either.left[Message, Employee]("Employee already available"))
      case None    =>
        listOfEmployees += empToInsert
        e.delay(Either.right[Message, Employee](empToInsert))
    }

  class EmployeeRepoImpl(implicit e: Sync[IO]) extends EmployeeRepo[IO] {
    override def getAllEmployees: IO[List[Employee]] =
      for {
        all <- e.delay(listOfEmployees.toList)
      } yield all

    override def addEmployee(employee: Employee): IO[Either[Message, Employee]] =
      for {
        isAvailable <- e.delay(listOfEmployees.find(_.id == employee.id))
        empEither <- tryInsertEmployee(isAvailable, employee)
        _ <- tryInsertAddress(empEither)
      } yield empEither
  }
}
