package com.myoperator

import com.coralogix.zio.k8s.client.com.myoperator.definitions.myresource.v1.MyResource
import com.coralogix.zio.k8s.client.com.myoperator.v1.myresources.MyResources
import com.coralogix.zio.k8s.client.model._
import com.coralogix.zio.k8s.client.v1.secrets.Secrets
import com.coralogix.zio.k8s.client.{K8sFailure, NamespacedResource}
import com.coralogix.zio.k8s.model.core.v1.Secret
import com.coralogix.zio.k8s.model.pkg.apis.meta.v1.ObjectMeta
import com.coralogix.zio.k8s.operator.Operator.EventProcessor
import com.coralogix.zio.k8s.operator._
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.SttpBackend
import zio._
import zio.clock.Clock
import zio.console.Console

import scala.language.postfixOps

trait MyOperator {
  def operator: Task[Operator[
    Clock,
    Throwable,
    MyResource
  ]]
}

case class MyOperatorLive(
    cnsl: Console.Service,
    sttp: SttpBackend[Task, ZioStreams with WebSockets],
    k8s: K8sCluster
) extends MyOperator {

  val eventProcessor: EventProcessor[Clock, Throwable, MyResource] =
    (ctx, event) =>
      event match {
        case Reseted() =>
          cnsl.putStrLn(s"Reseted - will (re) add any existing").ignore
        case Added(item) =>
          cnsl.putStrLn(s"Added - action on ${item}").ignore
        case Modified(item) =>
          cnsl.putStrLn(s"Modified - action on ${item}").ignore
        case Deleted(item) =>
          cnsl.putStrLn(s"Deleted - action on ${item}").ignore
      }

  val dbClientLayer
      : ZLayer[Any, Throwable, Has[NamespacedResource[MyResource]]] =
    (ZLayer.succeed(k8s) ++ ZLayer.succeed(sttp)) >>> MyResources.live.map(
      _.get.asGeneric
    )

  override def operator: Task[Operator[Clock, Throwable, MyResource]] =
    (
      Operator
        .namespaced(
          eventProcessor
        )(namespace = None, buffer = 1024)
      )
      .provideLayer(dbClientLayer)

}

object MyOperator extends Accessible[MyOperator] {
  val live: URLayer[Has[Console.Service] with Has[
    SttpBackend[Task, ZioStreams with WebSockets]
  ] with Has[
    K8sCluster
  ], Has[MyOperator]] = (MyOperatorLive.apply _).toLayer
}
