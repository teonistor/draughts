package io.github.teonistor.bsms.data

import org.scalatest.funsuite.AnyFunSuiteLike

class ShootingEffectTest extends AnyFunSuiteLike {

  test("miss") {
    assert(ShootingEffect.miss.isInstanceOf[ShootingEffect])
  }

  test("fatal hit") {
    val effect = ShootingEffect.fatal("Fishing boat")

    assert(effect.isInstanceOf[ShootingEffect.Hit])
    assert(effect.name == "Fishing boat")
    assert(effect.fatal)
  }

  test("nonfatal hit") {
    val effect = ShootingEffect.hit("Netting boat")

    assert(effect.isInstanceOf[ShootingEffect.Hit])
    assert(effect.name == "Netting boat")
    assert(!effect.fatal)
  }
}
