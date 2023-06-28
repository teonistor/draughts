package org.mockito.internal.debugging

import org.mockito.invocation.Location
import org.mockito.scalatest.MockitoSugar

// Shunt for a missing class somehow
object LocationFactory extends MockitoSugar {
  def create() = mock[Location]
}
