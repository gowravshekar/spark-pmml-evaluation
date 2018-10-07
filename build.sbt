scalaVersion := "2.11.12"
name := "iris"
version := "0.0.1"

val sparkVersion = "2.3.2"

libraryDependencies ++= Seq(
  "io.hydrosphere" %% "mist-lib" % "1.0.0-RC18",
  "org.jpmml" % "jpmml-evaluator-spark" % "1.2.0",
  "org.apache.spark" %% "spark-mllib" % "2.3.2",
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-hive" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided"
)

assemblyJarName in assembly := s"${name.value}_${version.value}.jar"

// https://stackoverflow.com/a/50036693/2052048
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
