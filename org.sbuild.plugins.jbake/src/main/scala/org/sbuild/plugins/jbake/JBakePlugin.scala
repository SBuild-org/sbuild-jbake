package org.sbuild.plugins.jbake

import de.tototec.sbuild._
import java.io.File
import de.tototec.sbuild.addons.support.ForkSupport

object JBakePlugin {

  case class JBakeVersion(version: String, zipUrl: String, jars: Seq[String])

  val jbakeVersions = Seq(
    JBakeVersion(
      version = "2.2.0",
      zipUrl = "http://jbake.org/files/jbake-2.2.0-bin.zip",
      jars = Seq(
        "jbake-core.jar",
        "lib/args4j-2.0.23.jar",
        "lib/asciidoctor-java-integration-0.1.4.jar",
        "lib/commons-configuration-1.9.jar",
        "lib/commons-io-2.4.jar",
        "lib/commons-lang-2.6.jar",
        "lib/commons-logging-1.1.1.jar",
        "lib/freemarker-2.3.19.jar",
        "lib/javax.servlet-3.0.0.v201112011016.jar",
        "lib/jcommander-1.30.jar",
        "lib/jetty-continuation-8.1.12.v20130726.jar",
        "lib/jetty-http-8.1.12.v20130726.jar",
        "lib/jetty-io-8.1.12.v20130726.jar",
        "lib/jetty-server-8.1.12.v20130726.jar",
        "lib/jetty-util-8.1.12.v20130726.jar",
        "lib/jruby-complete-1.7.4.jar",
        "lib/markdownj-0.3.0-1.0.2b4.jar"
      ).map("jbake-2.2.0/" + _)
    )
  )

}

class JBakePlugin(implicit project: Project) extends Plugin[JBake] {

  override def create(name: String): JBake = {
    val jbake = JBake(
      bakeTargetName = if (name == "") "jbake" else s"jbake-${name}",
      serveTargetName = if (name == "") "serve-jbake" else s"serve-jbake-${name}",
      sourceDir = Path("src") / (if (name == "") "jbake" else s"jbake-${name}"),
      targetDir = Path("target") / (if (name == "") "jbake" else s"site-${name}")
    )
    jbake
  }

  override def applyToProject(instances: Seq[(String, JBake)]): Unit = instances foreach {
    case (name, jbake) =>

      val jbakeCp: TargetRefs = jbake.jbakeHome match {
        case None =>
          JBakePlugin.jbakeVersions.find(_.version == jbake.jbakeVersion) match {
            case Some(v) =>
              Target("phony:jbake-classpath") dependsOn v.jars.map(j => TargetRef(s"zip:file=${j};archive=${v.zipUrl}"))
            case x =>
              throw new RuntimeException(s"Unsupported JBake version in 'jbakeVersion' property.\nPlease use one of the following: ${JBakePlugin.jbakeVersions.map(_.version).mkString(",")}.\nAlternatively you can use a locally installed JBake with the 'jbakeHome' property.")

          }
        case Some(home) =>
          (home / "jbake-core.jar") ~
            s"scan:${home / "lib"};regex=.*\\.jar"
      }

      val deps = s"scan:${jbake.sourceDir.getPath}"

      Target("phony:" + jbake.bakeTargetName) dependsOn jbakeCp ~ deps exec {
        jbake.targetDir.mkdirs
        ForkSupport.runJavaAndWait(
          classpath = jbakeCp.files,
          arguments = Array("org.jbake.launcher.Main", jbake.sourceDir.getPath, jbake.targetDir.getPath),
          failOnError = true
        )
      }

      Target("phony:" + jbake.serveTargetName) dependsOn jbakeCp ~ jbake.bakeTargetName exec {
        ForkSupport.runJavaAndWait(
          classpath = jbakeCp.files,
          arguments = Array("org.jbake.launcher.Main", "-s", jbake.targetDir.getPath),
          failOnError = true
        )

      }
  }
}