package io.github.tristanjl.deviceapi

import cats.effect._
import cats.implicits._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.tristanjl.deviceapi.entities.DeviceModels.DeviceId
import io.github.tristanjl.deviceapi.entities.{Device, DeviceWithId}

import java.io._
import scala.collection.mutable.HashMap
import scala.io.Source

final case class DeviceRepository[F[_]](
    private val devices: HashMap[String, DeviceWithId]
)(
    implicit e: Effect[F]
) {
  def getDevice(id: DeviceId): F[Option[DeviceWithId]] =
    e.delay { devices.get(id.hash) }

  def addDevice(device: Device): F[DeviceId] = {
    val deviceWithId = device.withId
    for {
      _ <- e.delay { devices += (deviceWithId.id.hash -> deviceWithId) }
    } yield deviceWithId.id
  }

  def upsertDevice(deviceWithId: DeviceWithId): F[Unit] = {
    for {
      _ <- e.delay { devices(deviceWithId.id.hash) = deviceWithId }
    } yield ()
  }

  def deleteDevice(deviceId: DeviceId): F[Unit] =
    e.delay { devices -= deviceId.hash }

  def saveToFile(filename: String): F[Unit] = e.delay {
    val file = new File(filename)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(devices.asJson.toString())
    bw.close()
  }
}

object DeviceRepository {
  def empty[F[_]](implicit e: Effect[F]): IO[DeviceRepository[F]] = IO {
    new DeviceRepository[F](HashMap())
  }

  def loadFromFile[F[_]](filename: String)(implicit e: Effect[F]): IO[DeviceRepository[F]] =
    IO {
      val file = Source.fromFile(filename)
      val devices = Decoder[HashMap[String, DeviceWithId]]
        .decodeJson(io.circe.parser.parse(file.getLines.mkString).getOrElse(Json.Null))
        .getOrElse(HashMap[String, DeviceWithId]())
      file.close

      new DeviceRepository[F](devices)
    }
}
