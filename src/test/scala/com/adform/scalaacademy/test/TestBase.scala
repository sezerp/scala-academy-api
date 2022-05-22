package com.adform.scalaacademy.test

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestBase extends AnyFlatSpec with Matchers {
  val testClock = new TestClock()
  val idPattern = "[a-zA-Z\\d]{64}"
}
