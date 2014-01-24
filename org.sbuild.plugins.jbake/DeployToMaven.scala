import de.tototec.sbuild._

@version("0.7.1")
@classpath(
  "mvn:org.apache.ant:ant:1.8.4",
  "../../maven-deploy/org.sbuild.plugins.mavendeploy/target/org.sbuild.plugins.mavendeploy-0.0.9000.jar"
)
class SBuild(implicit _project: Project) {

  val namespace = "org.sbuild.plugins.jbake"
  val version = "0.1.0"
  val url = "https://github.com/SBuild-org/sbuild-jbake"
  val sourcesDir = "src/main/scala"

  import org.sbuild.plugins.mavendeploy._

  Plugin[MavenDeploy] configure { _.copy(
    groupId = "org.sbuild",
    artifactId = namespace,
    version = version,
    artifactName = Some("SBuild JBake Plugin"),
    description = Some("An SBuild Plugin that integrates JBake static site generator."),
    repository = Repository.SonatypeOss,
    scm = Option(Scm(url = url, connection = url)),
    developers = Seq(Developer(id = "TobiasRoeser", name = "Tobias Roeser", email = "le.petit.fou@web.de")),
    gpg = true,
    licenses = Seq(License.Apache20),
    url = Some(url),
    files = Map(
      "jar" -> s"target/${namespace}-${version}.jar",
      "sources" -> s"target/${namespace}-${version}-sources.jar",
      "javadoc" -> "target/fake.jar"
    )
  )}

  Target(sourcesZip) dependsOn s"scan:${sourcesDir}" ~ "LICENSE.txt" exec { ctx: TargetContext =>
    AntZip(destFile = ctx.targetFile.get, fileSets = Seq(
      AntFileSet(dir = Path(sourcesDir)),
      AntFileSet(file = Path("LICENSE.txt"))
    ))
  }

  Target("target/fake.jar") dependsOn "LICENSE.txt" exec { ctx: TargetContext =>
    import de.tototec.sbuild.ant._
    tasks.AntJar(destFile = ctx.targetFile.get, fileSet = AntFileSet(file = "LICENSE.txt".files.head))
  }

}
