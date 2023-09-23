package io.github.teonistor.commongaming

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import org.mockito.BDDMockito.`given`
import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class GameConfigurationTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("create") {
    val om = mock[ObjectMapper]
    val holder = mock[GamesHolder[TestGame]]
    val factory = mock[TestSettings => TestGame]
    val settings = mock[JsonNode]
    given(om.treeToValue(settings, classOf[TestSettings])) willReturn TestSettings()
    given(factory(TestSettings())) willReturn TestGame()

    val config = new SimpleGameConfiguration("Test Game", Set("x", "y", "z"), om, holder, factory)
    config.create(1108L, settings)

    verify(holder).add(1108L, TestGame())
  }

  private case class TestSettings()
  private case class TestGame()
}
