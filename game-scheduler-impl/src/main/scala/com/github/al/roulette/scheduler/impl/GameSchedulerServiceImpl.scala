package com.github.al.roulette.scheduler.impl

import com.github.al.logging.EventLogging
import com.github.al.roulette.scheduler.api.{GameFinished, GameSchedulerService, GameStarted, ScheduledGameEvent}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Future

class GameSchedulerServiceImpl(pubSubRegistry: PubSubRegistry)
  extends GameSchedulerService
    with EventLogging
    with LazyLogging {
  private val internalGameScheduledEventsTopic = pubSubRegistry.refFor(TopicId[ScheduledGameEvent])

  override def scheduledEvents: Topic[ScheduledGameEvent] = TopicProducer.singleStreamWithOffset { offset =>
    internalGameScheduledEventsTopic.subscriber.filter {
      case _: GameStarted | _: GameFinished => true
      case _ => false
    }.mapAsync(1)(logEventAsync).mapAsync(1)(event => Future.successful(event -> offset))
  }
}
