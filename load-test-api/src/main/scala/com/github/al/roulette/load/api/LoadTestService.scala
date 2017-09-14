package com.github.al.roulette.load.api

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait LoadTestService extends Service {
  def startLoadTest: ServiceCall[LoadTestParameters, Source[String, NotUsed]]

  final override def descriptor: Descriptor = {
    import Service._

    named("load-test").withCalls(
      pathCall("/load-test/start", startLoadTest)
    ).withAutoAcl(true)
  }
}

