import sbt.Keys._
import sbt.{Cross, Load, Project, State}
import sbtrelease.Git
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._
import sbtassembly.AssemblyKeys._

object Release {
  val HashSize: Int = 6

  lazy val crossAssembly: ReleaseStep = ReleaseStep(action = st => {
    val crossBuild = Project.extract(st).get(releaseCrossBuild)
    if (crossBuild) {
      runCrossBuild(assembleJar.action)(st)
    } else {
      assembleJar.action(st)
    }
  })

  lazy val assembleJar: ReleaseStep = ReleaseStep(action = st => {
    val extracted = Project.extract(st)
    val (newState, _) = extracted.runTask(assembly, st)
    newState
  })

  private def switchScalaVersion(state: State, version: String): State = {
    val x = Project.extract(state)
    import x._
    state.log.info("Setting scala version to " + version)
    val add = (scalaVersion in sbt.GlobalScope := version) :: (scalaHome in sbt.GlobalScope := None) :: Nil
    val cleared = session.mergeSettings.filterNot(Cross.crossExclude)
    val newStructure = Load.reapply(add ++ cleared, structure)
    Project.setProject(session, newStructure, state)
  }

  private def runCrossBuild(func: State => State): State => State = { state =>
    val x = Project.extract(state)
    import x._
    val versions = Cross.crossVersions(state)
    val current = scalaVersion in currentRef get structure.data
    val finalS = (state /: versions) {
      case (s, v) => func(switchScalaVersion(s, v))
    }
    current.map(switchScalaVersion(finalS, _)).getOrElse(finalS)
  }

  lazy val customReleaseSteps = Seq[ReleaseStep](
    checkSnapshotDependencies,
    runClean,
    runTest,
    inquireVersions,
    setReleaseVersion,
    commitReleaseVersion,
    crossAssembly,
    tagRelease,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )

  def assemblyVersion(version: String, headCommit: Option[String]) =
    headCommit match {
      case Some(hash) if version.endsWith("-SNAPSHOT")  => s"$version-${hash.take(HashSize)}"
      case _                                            => version
    }
}