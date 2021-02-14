package io.github.tristanjl.deviceapi

import cats.effect.{IO, _}
import cats.implicits._
import fs2.{Stream, StreamApp}
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.tristanjl.deviceapi.entities.{Device, _}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.implicitConversions

object DeviceServer extends StreamApp[IO] with Http4sDsl[IO] {

  implicit def stringToDeviceId(string: String) = DeviceModels.DeviceId(string)

  val DEVICES = "devices"

  def service[F[_]](devices: DeviceRepository[F])(implicit F: Effect[F]): HttpService[F] =
    HttpService[F] {
      case GET -> Root / DEVICES / deviceId =>
        devices
          .getDevice(deviceId)
          .flatMap {
            case Some(device) =>
              Response(status = Status.Ok).withBody(device.asJson)
            case None => F.pure(Response(status = Status.NotFound))
          }

      case req @ POST -> Root / DEVICES =>
        req
          .decodeJson[Device]
          .flatMap(devices.addDevice)
          .flatMap(
            deviceId => Response(status = Status.Created).withBody(deviceId.asJson)
          )

      case req @ PUT -> Root / DEVICES =>
        req
          .decodeJson[DeviceWithId]
          .flatMap(devices.upsertDevice)
          .flatMap(_ => F.pure(Response(status = Status.Ok)))

      case DELETE -> Root / DEVICES / deviceId =>
        devices
          .deleteDevice(deviceId)
          .flatMap(_ => F.pure(Response(status = Status.NoContent)))
    }

  def stream(
      args: List[String],
      requestShutdown: IO[Unit]
  ): Stream[IO, StreamApp.ExitCode] = {
    val saveFile = "store.json"
    Stream.eval(DeviceRepository.loadFromFile[IO](saveFile)).flatMap { deviceRepo =>
      BlazeBuilder[IO]
        .bindHttp(8080, "0.0.0.0")
        .mountService(service(deviceRepo), "/")
        .serve
        .onFinalize(deviceRepo.saveToFile(saveFile))
    }
  }
}
