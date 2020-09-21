name := "employer-account-service"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % "0.9.0",
  "org.tpolecat" %% "doobie-h2" % "0.9.0",
  "org.tpolecat" %% "doobie-hikari" % "0.9.0",
  "org.tpolecat" %% "doobie-quill" % "0.9.0",

  "org.http4s" %% "http4s-blaze-server" % "0.21.1",
  "org.http4s" %% "http4s-circe" % "0.21.1",
  "org.http4s" %% "http4s-dsl" % "0.21.1",

  "org.http4s" %% "rho-swagger" % "0.21.0-RC1",
  "org.http4s" %% "rho-swagger-ui" % "0.21.0-RC1",

  "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC12",
  "dev.zio" %% "zio" % "1.0.0-RC18-2",
  "dev.zio" %% "zio-test" % "1.0.0-RC18-2" % "test",
  "dev.zio" %% "zio-test-sbt" % "1.0.0-RC18-2" % "test",
  "dev.zio" %% "zio-test-magnolia" % "1.0.0-RC18-2" % "test", // optional

  "com.github.pureconfig" %% "pureconfig" % "0.13.0",

  "io.circe" %% "circe-generic" % "0.13.0",
  "io.circe" %% "circe-generic-extras" % "0.13.0",

  "com.h2database" % "h2" % "1.4.199",
  "mysql" % "mysql-connector-java" % "8.0.13",

  "org.slf4j" % "slf4j-log4j12" % "1.7.26" % "runtime",
  "io.getquill" %% "quill-jdbc" % "3.5.2",

)

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalacOptions ++= Seq(
  //"-Xfatal-warnings",
  "-Ymacro-annotations"
)
