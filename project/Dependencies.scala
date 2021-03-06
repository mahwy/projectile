  import sbt._

object Dependencies {
  object Play {
    private[this] val version = "2.6.21"
    val filters = play.sbt.PlayImport.filters
    val guice = play.sbt.PlayImport.guice
    val cache = play.sbt.PlayImport.ehcache
    val json = "com.typesafe.play" %% "play-json" % "2.7.1"

    val all = Seq(filters, guice, cache, json)
  }

  object Database {
    val postgres = "org.postgresql" % "postgresql" % "42.2.5"
    val hikariCp = "com.zaxxer" % "HikariCP" % "3.2.0"
    val flyway = "org.flywaydb" % "flyway-core" % "5.2.4"

    object Slick {
      val version = "3.3.0"
      val pgVersion = "0.17.2"

      val core = "com.typesafe.slick" %% "slick" % version
      val hikariCp = "com.typesafe.slick" %% "slick-hikaricp" % version
      val pg = "com.github.tminglei" %% "slick-pg" % pgVersion
      val pgCirce = "com.github.tminglei" %% "slick-pg_circe-json" % pgVersion
      val slickless = "io.underscore" %% "slickless" % "0.3.3"

      val all = Seq(core, hikariCp, pg, pgCirce, slickless)
    }

    object Doobie {
      val version = "0.6.0"

      val core = "org.tpolecat" %% "doobie-core" % version
      val hikariCp = "org.tpolecat" %% "doobie-hikari" % version
      val postgres = "org.tpolecat" %% "doobie-postgres" % version
      val testing = "org.tpolecat" %% "doobie-scalatest" % version % "test"

      val all = Seq(core, hikariCp, postgres, testing)
    }
  }

  object GraphQL {
    val sangria = "org.sangria-graphql" %% "sangria" % "1.4.2"
    val playJson = "org.sangria-graphql" %% "sangria-play-json" % "1.0.5"
    val circe = "org.sangria-graphql" %% "sangria-circe" % "1.2.1"
  }

  object Serialization {
    val version = "0.11.1"
    val projects = Seq("circe-core", "circe-generic", "circe-generic-extras", "circe-parser", "circe-java8")
    val jackson = "io.circe" %% "circe-jackson29" % "0.11.0"
  }

  object Thrift {
    object TwitterBijection {
      val version = "0.9.6"
      val core = "com.twitter" %% "bijection-core" % version
      val util = "com.twitter" %% "bijection-util" % version
      val all = Seq(core, util)
    }

    object Finagle {
      val version = "18.12.0"

      val core = "com.twitter" %% "finagle-core" % version
      val thrift = "com.twitter" %% "finagle-thrift" % version
      val thriftMux = "com.twitter" %% "finagle-thriftmux" % version

      val all = Seq(core, thrift, thriftMux)
    }

    val all = TwitterBijection.all ++ Finagle.all
  }

  object Metrics {
    val version = "1.1.3"
    val micrometerCore = "io.micrometer" % "micrometer-core" % version
    val micrometerPrometheus = "io.micrometer" % "micrometer-registry-prometheus" % version
    val micrometerStatsd = "io.micrometer" % "micrometer-registry-statsd" % version
  }

  object Tracing {
    val version = "0.33.1"
    val jaegerCore = "io.jaegertracing" % "jaeger-core" % version
    val jaegerThrift = "io.jaegertracing" % "jaeger-thrift" % version
    val jaegerMetrics = "io.jaegertracing" % "jaeger-micrometer" % version
    val datadogTracing = "com.datadoghq" % "dd-trace-ot" % "0.24.0"
  }

  object Authentication {
    private[this] val version = "5.0.7"
    val silhouette = "com.mohiva" %% "play-silhouette" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")
    val hasher = "com.mohiva" %% "play-silhouette-password-bcrypt" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")
    val persistence = "com.mohiva" %% "play-silhouette-persistence" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")
    val crypto = "com.mohiva" %% "play-silhouette-crypto-jca" % version excludeAll ExclusionRule(organization = "com.atlassian.jwt")

    val all = Seq(silhouette, hasher, persistence, crypto)
  }

  object WebJars {
    val autocomplete = "org.webjars.bower" % "EasyAutocomplete" % "1.3.3" intransitive()
    val fontAwesome = "org.webjars" % "font-awesome" % "4.7.0" intransitive()
    val jquery = "org.webjars" % "jquery" % "2.2.4" intransitive()
    val materialize = "org.webjars" % "materializecss" % "1.0.0" intransitive()
    val swaggerUi = "org.webjars" % "swagger-ui" % "3.20.8" intransitive()

    val all = Seq(autocomplete, fontAwesome, jquery, materialize, swaggerUi)
  }

  object Utils {
    val enumeratumCirceVersion = "1.5.20"
    val booPickleVersion = "1.2.5"

    val betterFiles = "com.github.pathikrit" %% "better-files" % "3.7.0"
    val chimney = "io.scalaland" %% "chimney" % "0.3.0"
    val clist = "org.backuity.clist" %% "clist-core"   % "3.5.0"
    val clistMacros = "org.backuity.clist" %% "clist-macros" % "3.5.0" % "provided"
    val commonsCodec = "commons-codec" % "commons-codec" % "1.12"
    val commonsIo = "commons-io" % "commons-io" % "2.6"
    val commonsLang = "org.apache.commons" % "commons-lang3" % "3.8.1"
    val csv = "com.github.tototoshi" %% "scala-csv" % "1.3.5"
    val enumeratum = "com.beachape" %% "enumeratum-circe" % enumeratumCirceVersion
    val guava = "com.google.guava" % "guava" % "27.0.1-jre"
    val guice = "com.google.inject" % "guice" % "4.2.2"
    val javaxInject = "javax.inject" % "javax.inject" % "1"
    val logging = "org.slf4j" % "slf4j-api" % "1.7.25"
    val reftree = "io.github.stanch" %% "reftree" % "1.3.0"
    val scalaGuice = "net.codingwell" %% "scala-guice" % "4.2.2"
    val slf4j = "org.slf4j" % "slf4j-api" % "1.7.26"
    val thriftParser = "com.facebook.swift" % "swift-idl-parser" % "0.23.1"
    val typesafeConfig = "com.typesafe" % "config" % "1.3.3"
  }

  object Testing {
    val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % "test"
  }
}
