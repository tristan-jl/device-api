package io.github.tristanjl.deviceapi

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import io.github.tristanjl.deviceapi.entities.{
  ConnectionDetails,
  Device,
  DeviceModels,
  DeviceWithId
}
import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

import scala.collection.mutable.HashMap

class DeviceSpec extends Specification {

  "Get Devices" >> {
    "return 200" >> {
      getDevicesReturns200()
    }
    "return device" >> {
      getDevicesReturnsDevice()
    }
  }

  "Post Devices" >> {
    "return 201" >> {
      postDeviceReturns201()
    }
  }

  "Put Devices" >> {
    "return 200" >> {
      putDeviceReturns200()
    }
  }

  "Delete Devices" >> {
    "return 204" >> {
      deleteDeviceReturns204()
    }
  }

  val connectionDetails = ConnectionDetails("ip address", "password")
  val deviceWithId =
    DeviceWithId(DeviceModels.DeviceId("123"), "Mountain Device", connectionDetails)
  val device = Device("Mountain Device", connectionDetails)

  val deviceRepo =
    DeviceRepository[IO](HashMap(DeviceModels.DeviceId("123").hash -> deviceWithId))

  def testService(): HttpService[IO] = DeviceServer.service[IO](deviceRepo)

  private[this] val retGetDevice: Response[IO] = {
    val getListings = Request[IO](Method.GET, Uri.uri("/devices/123"))
    testService.orNotFound(getListings).unsafeRunSync()
  }

  private[this] def getDevicesReturns200(): MatchResult[Status] =
    retGetDevice.status must beEqualTo(Status.Ok)

  private[this] def getDevicesReturnsDevice(): MatchResult[String] = {
    val device = Json.fromString(
      """{"id":{"id":"123"},"name":"Mountain Device","connectionDetails":{"address":"ip address","password":"password"}}"""
    )
    retGetDevice.as[String].unsafeRunSync() must beEqualTo(device.asString.get)
  }

  private[this] val retPostDevice: Response[IO] = {
    val postListings = Request[IO](Method.POST, Uri.uri("/devices"))
      .withBody(device.asJson)
      .unsafeRunSync()
    testService().orNotFound(postListings).unsafeRunSync()
  }

  private[this] def postDeviceReturns201(): MatchResult[Status] =
    retPostDevice.status must beEqualTo(Status.Created)

  private[this] def retPutDevice: Response[IO] = {
    val putListing = Request[IO](Method.PUT, Uri.uri("/devices"))
      .withBody(deviceWithId.asJson)
      .unsafeRunSync()
    testService().orNotFound(putListing).unsafeRunSync()
  }

  private[this] def putDeviceReturns200(): MatchResult[Status] =
    retPutDevice.status must beEqualTo(Status.Ok)

  private[this] def retDeleteDevice: Response[IO] = {
    val delListing = Request[IO](Method.DELETE, Uri.uri("/devices/1234"))
    testService.orNotFound(delListing).unsafeRunSync()
  }

  private[this] def deleteDeviceReturns204(): MatchResult[Status] =
    retDeleteDevice.status must beEqualTo(Status.NoContent)

}
