package io.github.tristanjl.deviceapi.entities

case class DeviceWithId(
    id: DeviceModels.DeviceId,
    name: String,
    connectionDetails: ConnectionDetails
)
