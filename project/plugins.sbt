credentials += Credentials(Path.userHome / ".bintray" / ".credentials")

resolvers += "JAnalyse Repository" at "http://www.janalyse.fr/repository/"
resolvers += Classpaths.sbtPluginReleases
resolvers += Resolver.url("internal-generic", new URL("https://dl.bintray.com/fyber/internal-generic"))(Resolver.ivyStylePatterns)



addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.2.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")

//addSbtPlugin("com.fyber" % "sbt-commons" % "0.3.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.8.5")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.0")

//addSbtPlugin("com.fyber" % "sbt-marathon" % "0.19")

addSbtPlugin("com.github.shmishleniy" % "sbt-deploy-ssh" % "0.1.3")

addSbtPlugin("com.lucidchart" % "sbt-cross" % "3.0")
