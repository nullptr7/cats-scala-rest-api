package com.github.nullptr7
package repo

import model.BookModels.{BookId, Message}
import model.{Book, BookWithId}

import cats.effect.IO

import scala.collection.mutable

trait BookRepo {

  val getAllBooks: IO[List[BookWithId]]

  def addBook(book: Book): IO[BookId]

  def getBook(bookId: BookId): IO[Option[BookWithId]]

  def updateBook(bookId: BookId, book: Book): IO[Either[Message, Book]]

  def deleteBook(bookId: BookId): IO[Either[Message, Book]]
}

object BookRepo {

  class BookRepoImpl extends BookRepo {
    val storage: mutable.HashMap[BookId, Book] = mutable.HashMap[BookId, Book]().empty

    override def addBook(book: Book): IO[BookId] = IO {
      val bookId = BookId()
      storage.put(bookId, book)
      bookId
    }

    override def updateBook(id: BookId, book: Book): IO[Either[Message, Book]] =
      for {
        bookOpt <- getBook(id)
        _ <- IO(bookOpt.toRight(s"Book with ${id.value} not found"))
        updatedBook = storage.put(id, book).toRight(s"Book with ${id.value} not found")
      } yield updatedBook

    override def getBook(bookId: BookId): IO[Option[BookWithId]] = IO {
      storage.get(bookId).map(x => BookWithId(bookId.value, x.title, x.author))
    }

    override def deleteBook(id: BookId): IO[Either[Message, Book]] =
      for {
        removedBook <- IO(storage.remove(id))
        result = removedBook.toRight(s"Book with ${id.value} not found")
      } yield result

    override val getAllBooks: IO[List[BookWithId]] = IO {
      storage.map { case (id, book) => BookWithId(id.value, book.title, book.author) }.toList
    }
  }

}
