package com.adform.scalaacademy.infrastructure

import cats.effect.IO
import com.adform.scalaacademy.device.DeviceModel.Device
import com.adform.scalaacademy.infrastructure.DB.{DBError, DBNotFoundError, DBQueryExecutionError}
import com.adform.scalaacademy.util.Id
import com.softwaremill.tagging.@@
import com.typesafe.scalalogging.StrictLogging

import java.util.concurrent.ConcurrentHashMap
import scala.util.Try

class DB extends StrictLogging {
  private val data: ConcurrentHashMap[Id @@ Device, Device] = new ConcurrentHashMap()

  def select(id: Id @@ Device): IO[Either[DBError, Device]] = {
    IO {
      Try(Option(data.get(id))).toEither match {
        case Right(maybeDevice) =>
          maybeDevice match {
            case Some(device) => Right(device)
            case None         => Left(DBNotFoundError(s"Device with id: '$id' not found."))
          }
        case Left(t) =>
          logger.error("Error occurred when try select data", t)
          Left(DBQueryExecutionError(t))
      }
    }
  }

  def insert(device: Device): IO[Either[DBError, Unit]] = {
    IO {
      Try(data.put(device.id, device)).toEither.left
        .map { t =>
          logger.error(s"Error occurred when try insert $device", t)
          DBQueryExecutionError(t)
        }
        .map(_ => ())
    }
  }
}

object DB {
  sealed trait DBError
  case class DBNotFoundError(message: String)        extends DBError
  case class DBQueryExecutionError(error: Throwable) extends DBError

}
