package io.github.teonistor.bsms.data

sealed trait Movement {
  def aligns(positions: Set[Position]): Boolean
  def move(position: Position): Position
}

object Movement {

  private def alignsAlongIndex(positions: Set[Position], index: Int) = {
    val calculation = positions.to(LazyList)
      .flatMap(_.zipWithIndex)
      .groupMap(_._2)(_._1).view
      .mapValues(_.toSet.size)
      .groupMap(_._1 == index)(_._2).view
      .mapValues(_.toSet).toMap

    calculation.contains(true) && (!calculation.contains(false) || calculation(false) == Set(1))
  }

  val up: Movement = new Movement {
    override def aligns(positions: Set[Position]): Boolean = alignsAlongIndex(positions, 1)
    override def move(position: Position): Position = Vector(position(0), position(1) - 1)
  }

  val left: Movement = new Movement {
    override def aligns(positions: Set[Position]): Boolean = alignsAlongIndex(positions, 0)
    override def move(position: Position): Position = Vector(position(0) - 1, position(1))
  }

  val down: Movement = new Movement {
    override def aligns(positions: Set[Position]): Boolean = alignsAlongIndex(positions, 1)
    override def move(position: Position): Position = Vector(position(0), position(1) + 1)
  }

  val right: Movement = new Movement {
    override def aligns(positions: Set[Position]): Boolean = alignsAlongIndex(positions, 0)
    override def move(position: Position): Position = Vector(position(0) + 1, position(1))
  }
}

