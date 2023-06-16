package $organization$

import com.coralogix.zio.k8s.client.$example_resource_group_extension$.$example_resource_group_organization$.definitions.$example_resource_name_singular$.v1.$example_resource_name_kind$
import com.coralogix.zio.k8s.client.$example_resource_group_extension$.$example_resource_group_organization$.v1.$example_resource_name_plural$.$example_resource_name_kind$s
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

trait $example_resource_name_kind$Controller {
  def operator: Task[Operator[
    Clock,
    Throwable,
    $example_resource_name_kind$
  ]]
}

case class $example_resource_name_kind$ControllerLive(
    cnsl: Console.Service,
    sttp: SttpBackend[Task, ZioStreams with WebSockets],
    k8s: K8sCluster
) extends $example_resource_name_kind$Controller {

  val eventProcessor: EventProcessor[Clock, Throwable, $example_resource_name_kind$] =
    (ctx, event) =>
      event match {
        case Reseted() =>
          cnsl.putStrLn(s"Reseted - will (re) add any existing").ignore
        case Added(item) =>
          cnsl.putStrLn(s"Added - action on \${item}").ignore
        case Modified(item) =>
          cnsl.putStrLn(s"Modified - action on \${item}").ignore
        case Deleted(item) =>
          cnsl.putStrLn(s"Deleted - action on \${item}").ignore
      }

  val clientLayer
      : ZLayer[Any, Throwable, Has[NamespacedResource[$example_resource_name_kind$]]] =
    (ZLayer.succeed(k8s) ++ ZLayer.succeed(sttp)) >>> $example_resource_name_kind$s.live.map(
      _.get.asGeneric
    )

  override def operator: Task[Operator[Clock, Throwable, $example_resource_name_kind$]] =
    (
      Operator
        .namespaced(
          eventProcessor
        )(namespace = None, buffer = 1024)
      )
      .provideLayer(clientLayer)

}

object $example_resource_name_kind$Controller extends Accessible[$example_resource_name_kind$Controller] {
  val live: URLayer[Has[Console.Service] with Has[
    SttpBackend[Task, ZioStreams with WebSockets]
  ] with Has[
    K8sCluster
  ], Has[$example_resource_name_kind$Controller]] = ($example_resource_name_kind$ControllerLive.apply _).toLayer
}
