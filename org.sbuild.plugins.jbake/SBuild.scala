import de.tototec.sbuild._
import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._
import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._

import de.tototec.sbuild._

@version("0.7.1")
@classpath(
  "mvn:org.sbuild:org.sbuild.plugins.sbuildplugin:0.2.2",
  "mvn:org.apache.ant:ant:1.8.4",
  "mvn:org.sbuild:org.sbuild.plugins.mavendeploy:0.1.0"
)
class SBuild(implicit _project: Project) {

  val namespace = "org.sbuild.plugins.jbake"
  val version = "0.1.0.9000"
  val url = "https://github.com/SBuild-org/sbuild-jbake"
  val sourcesJar = s"target/${namespace}-${version}-sources.jar"
  val sourcesDir = "src/main/scala"

  Target("phony:all") dependsOn "jar" ~ sourcesJar

  Plugin[org.sbuild.plugins.sbuildplugin.SBuildPlugin] configure { _.copy(
    sbuildVersion = "0.7.1",
    pluginClass = s"${namespace}.JBake",
    pluginVersion = version,
    manifest = Map("SBuild-Version" -> "0.7.1.9000")
  )}


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

  Target(sourcesJar) dependsOn s"scan:${sourcesDir}" ~ "LICENSE.txt" exec { ctx: TargetContext =>
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
