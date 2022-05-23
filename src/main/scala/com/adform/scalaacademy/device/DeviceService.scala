package com.adform.scalaacademy.device

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeErrorId
import com.adform.scalaacademy.Fail
import com.adform.scalaacademy.device.DeviceModel.{Device, DeviceRules}
import com.adform.scalaacademy.infrastructure.{DB, IdGenerator}
import com.adform.scalaacademy.infrastructure.DB.{DBNotFoundError, DBQueryExecutionError}
import com.adform.scalaacademy.logging.FLogging
import com.adform.scalaacademy.util.{Clock, Id}
import com.softwaremill.tagging.@@
import cats.effect.std.Random

import scala.concurrent.duration.FiniteDuration

class DeviceService(db: DB, idGenerator: IdGenerator, clock: Clock, deviceServiceErrorGen: DeviceServiceErrorGen)
    extends FLogging {
  def get(id: Id @@ Device): IO[Device] = {
    for {
      _       <- deviceServiceErrorGen.delay()
      _       <- deviceServiceErrorGen.passOrRaiseError()
      now     <- clock.now[IO]()
      _       <- logger.info(s"Selecting device with $id at $now")
      deviceE <- db.select(id)
      device <- deviceE.fold(
        {
          case _: DBQueryExecutionError => Fail.InternalServerError.raiseError[IO, Device]
          case err: DBNotFoundError     => Fail.NotFound(err.message).raiseError[IO, Device]
        },
        device => IO(device)
      )
    } yield device
  }

  def create(rules: DeviceRules): IO[Device] = {
    for {
      _   <- deviceServiceErrorGen.delay()
      _   <- deviceServiceErrorGen.passOrRaiseError()
      id  <- idGenerator.nextId[IO, Device]()
      now <- clock.now[IO]()
      device = Device(id = id, rules = rules, createdAt = now)
      _      <- logger.info(s"Creating new device with id: $id")
      result <- db.insert(device)
      _      <- result.fold(_ => Fail.InternalServerError.raiseError[IO, Unit], _ => IO(()))
    } yield device
  }
}

class DeviceServiceErrorGen(conf: DeviceErrorSimulatorConf) extends FLogging {

  private val random: IO[Random[IO]] = Random.scalaUtilRandom[IO]
  private val errors: Array[Fail] = Array(
    Fail.InternalServerError,
    Fail.Forbidden,
    Fail.IncorrectInput("""Synthetic error
                           |░░░░░▄▄▄▄▀▀▀▀▀▀▀▀▄▄▄▄▄▄░░░░░░░
                           |░░░░░█░░░░▒▒▒▒▒▒▒▒▒▒▒▒░░▀▀▄░░░░
                           |░░░░█░░░▒▒▒▒▒▒░░░░░░░░▒▒▒░░█░░░
                           |░░░█░░░░░░▄██▀▄▄░░░░░▄▄▄░░░░█░░
                           |░▄▀▒▄▄▄▒░█▀▀▀▀▄▄█░░░██▄▄█░░░░█░
                           |█░▒█▒▄░▀▄▄▄▀░░░░░░░░█░░░▒▒▒▒▒░█
                           |█░▒█░█▀▄▄░░░░░█▀░░░░▀▄░░▄▀▀▀▄▒█
                           |░█░▀▄░█▄░█▀▄▄░▀░▀▀░▄▄▀░░░░█░░█░
                           |░░█░░░▀▄▀█▄▄░█▀▀▀▄▄▄▄▀▀█▀██░█░░
                           |░░░█░░░░██░░▀█▄▄▄█▄▄█▄████░█░░░
                           |░░░░█░░░░▀▀▄░█░░░█░█▀██████░█░░
                           |░░░░░▀▄░░░░░▀▀▄▄▄█▄█▄█▄█▄▀░░█░░
                           |░░░░░░░▀▄▄░▒▒▒▒░░░░░░░░░░▒░░░█░
                           |░░░░░░░░░░▀▀▄▄░▒▒▒▒▒▒▒▒▒▒░░░░█░
                           |░░░░░░░░░░░░░░▀▄▄▄▄▄░░░░░░░░█░░
                           |""".stripMargin)
  )

  def passOrRaiseError(): IO[Unit] = {
    for {
      error            <- selectError()
      shouldRaiseError <- shouldRaiseError()
      _                <- if (shouldRaiseError) logger.info(s"The $error error will be raised") else IO(())
      _                <- if (shouldRaiseError) error.raiseError[IO, Unit] else IO(())
    } yield ()
  }

  def delay(): IO[Unit] = {
    for {
      delayGen <- random
      delay    <- delayGen.betweenLong(0, conf.maxTimeout.toMillis)
      _        <- logger.info(s"Request artificially delayed by $delay [ms]")
      _        <- IO.sleep(FiniteDuration.apply(delay, java.util.concurrent.TimeUnit.MILLISECONDS))
    } yield ()
  }

  private def shouldRaiseError(): IO[Boolean] = {
    for {
      randomGen <- random
      num       <- randomGen.nextDouble
      shouldError = conf.errorProbability < num
    } yield shouldError
  }

  private def selectError(): IO[Fail] = {
    for {
      randomGen <- random
      idx       <- randomGen.betweenInt(0, errors.length)
      err = errors(idx)
    } yield err
  }
}
