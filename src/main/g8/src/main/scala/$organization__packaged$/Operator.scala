package $organization$

import com.coralogix.zio.k8s.client.config.httpclient._
import com.coralogix.zio.k8s.client.v1.secrets.Secrets
import zio._
import zio.clock.Clock
import zio.logging.Logging
import zio.magic._
import $organization$.$example_resource_name_kind$Controller

object Operator extends App {

  val operatorProgram: ZIO[Has[
    $example_resource_name_kind$Controller
  ] with Clock with Logging, Nothing, ExitCode] = {
    for {
      op <- $example_resource_name_kind$Controller(_.operator)
      _  <- op.start()
      _  <- ZIO.never
    } yield ExitCode.success
  }.catchAll(_ => ZIO.succeed(ExitCode.failure))

  val operatorLayer = ZLayer.wire[Has[
    $example_resource_name_kind$Controller
  ] with Clock with Logging](
    ZEnv.live,
    Logging.ignore,
    k8sDefault,
    $example_resource_name_kind$Controller.live
  )
  override def run(args: List[String]): zio.URIO[zio.ZEnv, zio.ExitCode] =
    operatorProgram.provideLayer(operatorLayer.orDie)

}
