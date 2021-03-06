package com.github.al.roulette.winnings.impl

import akka.stream.scaladsl.Flow
import com.github.al.logging.EventLogging
import com.github.al.persistence.PersistentEntityRegistrySugar
import com.github.al.roulette.game.api.{GameResulted, GameService}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

class GameResultEventsSubscriber(gameService: GameService,
                                 override val entityRegistry: PersistentEntityRegistry)
                                (implicit executionContext: ExecutionContext)
  extends PersistentEntityRegistrySugar
    with EventLogging
    with LazyLogging {

  gameService.gameResultEvents.subscribe.atLeastOnce(Flow[GameResulted].mapAsync(1)(logEventAsync).mapAsync(1)(
    e => entityRef[WinningsEntity](e.gameId).ask(SaveGameResult(e.winningNumber))
  ))
}
