package com.github.nullptr7
package model

case class Address(
                    addressLine1: Option[String] = None,
                    addressLine2: Option[String] = None,
                    pinCode: Option[Long] = None
                  )
