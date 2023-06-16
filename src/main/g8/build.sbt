lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := "2.13.8",
    organization := "$organization$",
    name         := "$name$",
    version      := "0.0.1",
    libraryDependencies ++= Seq(
      "com.coralogix"                 %% "zio-k8s-operator"       % "1.4.3",
      "com.coralogix"                 %% "zio-k8s-client"         % "1.4.3",
      "com.softwaremill.sttp.client3" %% "httpclient-backend-zio" % "3.3.18", //
      "com.softwaremill.sttp.client3" %% "slf4j-backend"          % "3.3.18",
      "io.github.kitlangton"          %% "zio-magic"              % "0.3.11"
    )
  )
  .enablePlugins(JavaServerAppPackaging)

externalCustomResourceDefinitions := Seq(
  file("crds/$example_resource_name_singular$.yaml")
)

enablePlugins(K8sCustomResourceCodegenPlugin)
