package io.github.teonistor.bsms.data

sealed trait ShootingEffect

// noinspection NameBooleanParameters
object ShootingEffect {

  sealed case class Hit private(name: String,
                                fatal: Boolean) extends ShootingEffect

  val miss: ShootingEffect = new ShootingEffect {}
  def hit(name: String): Hit = Hit(name, false)
  def fatal(name: String): Hit = Hit(name, true)
}
