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
      fanOut0(input, `+-1`)

    override def promote: Piece = this
  }

  private class Peon(forward: Int, val promote: Piece) extends Piece with PieceBase {
    protected def fanOut(input: Vector[Int]): Iterable[Vector[Int]] =
      fanOut0(input, Some(forward))
  }

  private def fanOut0(input: Vector[Int], lastMovement: Iterable[Int]) =
    fanOut1(Vector(input), input.size - 1, lastMovement, 0)

  @tailrec
  private def fanOut1(accum: Vector[Vector[Int]], lastIndex: Int, lastMovement: Iterable[Int], i: Int): Iterable[Vector[Int]] =
    if (i < lastIndex)
      fanOut1(accum.flatMap(v => `+-1`.map(d => v.updated(i, v(i) + d))), lastIndex, lastMovement, i + 1)
    else
      accum.flatMap(v => lastMovement.map(d => v.updated(i, v(i) + d)))

  private def emitSlidesInOneDirection(from: Vector[Int], firstSteps: Iterable[Vector[Int]]) =
    firstSteps.map(to => (to, Move.Sliding(from, to)))

  private def emitJumpsInOneDirection(from: Vector[Int], firstSteps: Iterable[Vector[Int]]) =
    firstSteps.map(over => {
      val to = secondStep(from, over)
      (to, Move.Jumping(from, over, to))
    })

  private def secondStep(origin:Vector[Int], firstStep:Vector[Int]) =
    Vector.tabulate(origin.size)(i => 2 * firstStep(i) - origin(i))
}
