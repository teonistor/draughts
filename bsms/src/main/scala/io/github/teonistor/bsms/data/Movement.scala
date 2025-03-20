package io.github.teonistor.bsms.data

sealed trait Movement {
  def move(input: Vector[Int]): Vector[Int]
}

object Movement {

  val up: Movement = new Movement {
    override def move(input: Vector[Int]): Vector[Int] = Vector(input(0), input(1) - 1)
  }
  val left: Movement = new Movement {
    override def move(input: Vector[Int]): Vector[Int] = Vector(input(0) - 1, input(1))
  }
  val down: Movement = new Movement {
    override def move(input: Vector[Int]): Vector[Int] = Vector(input(0), input(1) + 1)
  }
  val right: Movement = new Movement {
    override def move(input: Vector[Int]): Vector[Int] = Vector(input(0) + 1, input(1))
  }
}

