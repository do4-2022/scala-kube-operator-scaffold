package com.myoperator

import com.coralogix.zio.k8s.client.config.httpclient._
import com.coralogix.zio.k8s.client.v1.secrets.Secrets
import zio._
import zio.clock.Clock
import zio.logging.Logging
import zio.magic._
import com.myoperator.MyOperator

object Operator extends App {

  val operatorProgram: ZIO[Has[
    MyOperator
  ] with Clock with Logging, Nothing, ExitCode] = {
    for {
      op <- MyOperator(_.operator)
      _  <- op.start()
      _  <- ZIO.never
    } yield ExitCode.success
  }.catchAll(_ => ZIO.succeed(ExitCode.failure))

  val operatorLayer = ZLayer.wire[Has[
    MyOperator
  ] with Clock with Logging](
    ZEnv.live,
    Logging.ignore,
    k8sDefault,
    MyOperator.live
  )
  override def run(args: List[String]): zio.URIO[zio.ZEnv, zio.ExitCode] =
    operatorProgram.provideLayer(operatorLayer.orDie)

}
