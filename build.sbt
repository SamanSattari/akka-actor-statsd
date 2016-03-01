val statsdSettings = Seq(
  organization := "com.thenewmotion",
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("https://github.com/thenewmotion/akka-actor-statsd")),
  libraryDependencies := {
    Dependencies(scalaVersion.value).commonTest
  }
)

def Dependencies(scalaVersion: String) = new {
  def akka(lib: String) = "com.typesafe.akka" %% s"akka-$lib" % {
    scalaVersion match {
      case tnm.ScalaVersion.curr => "2.4.2"
      case tnm.ScalaVersion.prev => "2.3.14"
    }
  }

  def spray(lib: String, v: String = "1.3.3") = "io.spray" %% s"spray-$lib" % v

  val ficus = "net.ceedubs" %%  "ficus" % {
    scalaVersion match {
      case tnm.ScalaVersion.curr => "1.1.2"
      case tnm.ScalaVersion.prev => "1.0.1"
    }
  }

  val statsdCore = Seq(
    akka("actor"),
    akka("slf4j"),
    ficus,
    "ch.qos.logback" % "logback-classic" % "1.1.6"
  )

  val statsdSpray = statsdCore ++ Seq(
    spray("http"),
    spray("routing-shapeless2"),
    spray("testkit") % "test"
  )

  val commonTest = Seq(
    akka("testkit"),
    "org.scalatest" %% "scalatest" % "2.2.6"
  ).map(_ % "test")
}

val `akka-statsd-core` = project
  .enablePlugins(OssLibPlugin)
  .settings(
    statsdSettings,
    libraryDependencies ++= Dependencies(scalaVersion.value).statsdCore
  )

val `akka-statsd-spray` = project
  .enablePlugins(OssLibPlugin)
  .dependsOn(`akka-statsd-core`)
  .settings(
    statsdSettings,
    libraryDependencies ++= Dependencies(scalaVersion.value).statsdSpray
  )

val `akka-statsd` =
  project.in(file("."))
  .enablePlugins(OssLibPlugin)
  .aggregate(`akka-statsd-core`, `akka-statsd-spray`)
  .settings(
    publish := {}
  )
