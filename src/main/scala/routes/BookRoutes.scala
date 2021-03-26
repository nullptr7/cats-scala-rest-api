package com.github.nullptr7
package routes

import model.BookModels._
import model.Book
import repo.BookRepo
import io.circe.Json
import cats.effect.IO
import org.http4s.HttpRoutes
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl

object BookRoutes {

  def routes(bookRepo: BookRepo): HttpRoutes[IO] = {

    val dsl = new Http4sDsl[IO] {}
    import dsl._

    HttpRoutes.of[IO] {
      case _@GET -> Root / "books" =>
        bookRepo.getAllBooks.flatMap(books => Ok(books))

      case _@GET -> Root / "books" / id =>
        bookRepo.getBook(BookId(id)).flatMap {
          case None       => NotFound(errorBody("Book Not Found"))
          case Some(book) => Ok(book)
        }

      case req@POST -> Root / "books" =>
        req.decode[Book] { book =>
          bookRepo.addBook(book).flatMap { id =>
            Created(Json.obj(("id", Json.fromString(id.value))))
          }
        }

      case req@PUT -> Root / "books" / id =>
        req.decode[Book] { book =>
          bookRepo.updateBook(BookId(id), book).flatMap {
            case Left(message) => NotFound(errorBody(message))
            case Right(book)   => Ok(book)
          }
        }

      case _@DELETE -> Root / "books" / id =>
        bookRepo.deleteBook(BookId(id)).flatMap {
          case Left(message) => NotFound(errorBody(message))
          case Right(book)   => Ok(book)
        }
    }

  }

  private[this] def errorBody(message: Message): Json = Json.obj(("message", Json.fromString(message)))

}
