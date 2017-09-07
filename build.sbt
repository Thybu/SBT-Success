name := "SBT-Success"

version := "0.13.6"

scalaVersion := "2.10.5"


import sbt.Keys._
import sbt._
import sbtassembly.{MergeStrategy, PathList}

val customResolvers = Seq(
  "Concurrent Maven Repo" at "http://conjars.org/repo",
  "Confluent" at "http://packages.confluent.io/maven/",
  "Cloudera" at "https://repository.cloudera.com/content/repositories/releases/",
  "Cloudera Artifactory" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  Resolver.bintrayRepo("fyber", "internal"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

def projectTemplate(projectName: String): Project = Project(projectName, file(projectName))
  .enablePlugins(GitVersioning, BuildInfoPlugin , ReleasePlugin)
  .settings(
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalaVersion := "2.10.5",
    fork in run := true,
    javaOptions in run ++= Seq("-Xmx2000m", "-Xms2000m"),
    outputStrategy := Some(StdoutOutput),
    testOptions in ThisBuild <+= (target in Test) map {
      t => Tests.Argument(TestFrameworks.ScalaTest, "-oFD", "-u", t + "/test-reports")
    },
    test in assembly := {},
    parallelExecution in test := false,
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    assemblyJarName in assembly := s"$projectName-v${Release.assemblyVersion(version.value, git.gitHeadCommit.value)}.jar",
    releaseProcess := Release.customReleaseSteps,
    releaseUseGlobalVersion := false,
    releaseVersionFile := file(projectName + "/version.sbt"),
    releaseTagName := s"$projectName-v${version.value}",
    git.useGitDescribe := true,
    publishTo := None,
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      scalaVersion,
      BuildInfoKey.action("version") {
        Release.assemblyVersion(version.value, git.gitHeadCommit.value)
      },
      BuildInfoKey.action("sha") {
        git.gitHeadCommit.value
      }
    ),
    buildInfoPackage := s"com.fyber.data.$projectName",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "org.apache.spark" %% "spark-core" % "1.6.0" % "provided",
      "org.apache.spark" %% "spark-sql" % "1.6.0",
      "org.apache.spark" %% "spark-hive" % "1.6.0",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.apache.hadoop" % "hadoop-hdfs" % "2.6.0-cdh5.9.0" % "test",
      "org.apache.hadoop" % "hadoop-common" % "2.6.0-cdh5.9.0" % "test",
      "org.apache.hadoop" % "hadoop-minicluster" % "2.6.0-cdh5.9.0" % "test",
      "org.mockito" % "mockito-all" % "1.9.5" % "test",
      "org.apache.kafka" % "kafka-clients" % "0.8.2-beta",
      "org.scalacheck" % "scalacheck_2.11" % "1.11.5" % "test"),

    resolvers ++= Seq(
      "JBoss Repository" at "http://repository.jboss.org/nexus/content/repositories/releases/",
      "Spray Repository" at "http://repo.spray.cc/",
      "Cloudera Repository" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
      "Twitter4J Repository" at "http://twitter4j.org/maven2/",
      "Apache HBase" at "https://repository.apache.org/content/repositories/releases",
      "Twitter Maven Repo" at "http://maven.twttr.com/",
      "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/",
      "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven",
      Resolver.sonatypeRepo("releases")
    )
  )




lazy val OnlyForAssembly = projectTemplate("OnlyForAssembly")
  .settings(
    mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
    {
      case m if m.toLowerCase.endsWith("manifest.mf") => MergeStrategy.discard
      case m if m.startsWith("META-INF") => MergeStrategy.discard
      case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
      case PathList("org", "apache", xs @ _*) => MergeStrategy.first
      case PathList("org", "jboss", xs @ _*) => MergeStrategy.first
      case "about.html"  => MergeStrategy.rename
      case "reference.conf" => MergeStrategy.concat
      case _ => MergeStrategy.first
    }
    }
  )
