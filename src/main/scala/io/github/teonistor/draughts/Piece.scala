package io.github.teonistor.draughts

import io.github.teonistor.draughts.move.Move

import scala.annotation.tailrec

sealed trait Piece {
  def emitMoves(from:Vector[Int]): Map[Vector[Int], Move]
  def emitJumps(from:Vector[Int]): Map[Vector[Int], Move]
  def promote: Piece
}

object Piece {
  private val `+-1` = List(1, -1)

  // DONTDO Ideally the concept of "forward" would be relegated to the board (via the specific player) but the static initialisaiton implications of that are asinine

  val blackKing:Piece = new King
  val blackPeon:Piece = new Peon(-1, blackKing)
  val whiteKing:Piece = new King
  val whitePeon:Piece = new Peon(+1, whiteKing)

  private trait PieceBase {
    def emitMoves(from: Vector[Int]): Map[Vector[Int], Move] = {
      val firstSteps = fanOut(from)
      (emitSlidesInOneDirection(from, firstSteps) ++ emitJumpsInOneDirection(from, firstSteps)).toMap
    }

    def emitJumps(from: Vector[Int]): Map[Vector[Int], Move] = 
      emitJumpsInOneDirection(from, fanOut(from)).toMap

    protected def fanOut(input: Vector[Int]): Iterable[Vector[Int]]
  }

  private class King extends Piece with PieceBase {
    protected def fanOut(input: Vector[Int]): Iterable[Vector[Int]] =
      fanOutRecu(Vector(input), `+-1`)

    override def promote: Piece = this
  }

  private class Peon(forward: Int, val promote: Piece) extends Piece with PieceBase {
    protected def fanOut(input: Vector[Int]): Iterable[Vector[Int]] =
      fanOutRecu(Vector(input), Some(forward))
  }

  @tailrec
  private def fanOutRecu(accum: Vector[Vector[Int]], lastOne: Iterable[Int], i: Int = 0): Iterable[Vector[Int]] =
    if (i < accum.head.size - 1)
      fanOutRecu(accum.flatMap(v => `+-1`.map(d => v.updated(i, v(i) + d))), lastOne, i + 1)
    else
      accum.flatMap(v => lastOne.map(d => v.updated(i, v(i) + d)))

  private def emitSlidesInOneDirection(from: Vector[Int], firstSteps: Iterable[Vector[Int]]) = {
    firstSteps.map(to => (to, Move.Sliding(from, to)))
//    to.ma
//    val x1 = dx + from.x
//    val y1 = dy + from.y
//    emission(from, Position(x1, y1), Position(dx + x1, dy + y1))
  }

//  private def emitAllMovesInOneDirection(from: Position, p1: Position, p2: Position) =
//    List(p1 -> Move.Sliding(from, p1),
//         p2 -> Move.Jumping(from, p1, p2))

  private def emitJumpsInOneDirection(from: Vector[Int], firstSteps: Iterable[Vector[Int]]) = {
    firstSteps.map(over => {
      val to = secondStep(from, over)
      (to, Move.Jumping(from, over, to))
    })
//    Some(p2 -> Move.Jumping(from, p1, p2))
  }

  private def secondStep(origin:Vector[Int], firstStep:Vector[Int]) =
    Vector.tabulate(origin.size)(i => 2 * firstStep(i) - origin(i))
}
