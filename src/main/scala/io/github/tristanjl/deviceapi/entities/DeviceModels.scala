package io.github.tristanjl.deviceapi.entities

import org.apache.commons.codec.digest.DigestUtils

import java.util.UUID

object DeviceModels {
  final case class DeviceId(id: String = UUID.randomUUID().toString) {
    override def toString: String = id

    val hash: String = DigestUtils.sha256Hex(id.getBytes)
  }
}
