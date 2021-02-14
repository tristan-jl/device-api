package io.github.tristanjl.deviceapi.entities

case class Device(name: String, connectionDetails: ConnectionDetails) {
  val withId = DeviceWithId(DeviceModels.DeviceId(), name, connectionDetails)
}
