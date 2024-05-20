package io.github.teonistor.ttt

import org.assertj.core.api.Assertions._
import org.junit.jupiter.api.Test

class GameOverCheckerTest {

  val gameOverChecker = new GameOverChecker

  @Test def blankBoardNoOneWins() {
    assertThat(gameOverChecker.check(GameState()).isEmpty).isTrue
  }

  @Test def fiveInLineAndFiveScatteredXWins() {
    val state = new GameState(Set((1, 0), (1, 1), (1, 2), (1, 3), (1, 4)),
      Set((0, 7), (1, -1), (0, 6), (0, 5)), Player.O)
    assertThat(gameOverChecker.check(state).get).isEqualTo(Player.X)
  }

  @Test def columnOWins() {
    val state = new GameState(Set((1, 0), (1, 2), (1, 3), (1, 4), (1, 6)),
      Set((-1, 5), (0, 5), (2, 5), (1, 5), (3, 5)), Player.X)
    assertThat(gameOverChecker.check(state).get).isEqualTo(Player.O)
  }

  @Test def fiveInLineOWins() {
    val state = new GameState(Set((0, 7), (1, -1), (0, 6), (0, 5), (-1, 6)),
      Set((3, 5), (3, 1), (3, 2), (3, 3), (3, 4)), Player.X)
    assertThat(gameOverChecker.check(state).get).isEqualTo(Player.O)
  }

  @Test def diagonalUpX() {
    val state = new GameState(Set((0, 7), (1, 6), (2, 5), (3, 4), (4, 3)),
      Set((3, 5), (5, 2), (3, 2), (3, 3)), Player.O)
    assertThat(gameOverChecker.check(state).get).isEqualTo(Player.X)
  }

  @Test def diagonalDownO() {
    val state = new GameState(Set((-1, 0), (-1, 3), (-1, 5), (1, 1), (2, 1)),
      Set((-2, 0), (-1, 1), (0, 2), (1, 3), (2, 4)), Player.X)
    assertThat(gameOverChecker.check(state).get).isEqualTo(Player.O)
  }

  @Test def noOneWins() {
    val state = new GameState(Set((-1, 0), (-1, 3), (1, 5), (1, 1), (2, 1), (-1, 2), (-2, -2)),
      Set((-2, 0), (-1, 1), (0, 2), (0, 3), (0, 4), (0, 0), (0, 5)), Player.X)
    assertThat(gameOverChecker.check(state).isEmpty).isTrue
  }
}
