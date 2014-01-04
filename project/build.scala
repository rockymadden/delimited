import sbt._
import Keys._

object CoreBuild extends Build {
	lazy val root = Project("delimited", file("."),
		settings = Defaults.defaultSettings ++ Seq(
			organization := "com.rockymadden.delimited",
			name := "delimited",
			version := "0.1.0",
			scalaVersion := "2.10.3",
			resolvers ++= Seq(DefaultMavenRepository),
			publishTo := Some("Sonatype" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"),
			publishMavenStyle := true,
			credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
			pomExtra :=
				<url>http://rockymadden.com/delimited/</url>
					<licenses>
						<license>
							<name>MIT</name>
							<distribution>repo</distribution>
						</license>
					</licenses>
					<scm>
						<url>git@github.com:rockymadden/delimited.git</url>
						<connection>scm:git:git@github.com:rockymadden/delimited.git</connection>
					</scm>
					<developers>
						<developer>
							<id>rockymadden</id>
							<name>Rocky Madden</name>
							<url>http://rockymadden.com/</url>
						</developer>
					</developers>)
	).aggregate(core)

	lazy val core: Project = Project("core", file("core"),
		settings = (root.settings: Seq[sbt.Def.Setting[_]]) ++ Seq(
			name := "delimited-core",
			libraryDependencies ++= Seq("org.specs2" %% "specs2" % "2.3.7" % "test")
		)
	)
}
