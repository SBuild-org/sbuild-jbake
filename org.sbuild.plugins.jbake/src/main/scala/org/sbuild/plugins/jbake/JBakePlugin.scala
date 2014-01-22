package org.sbuild.plugins.jbake

import de.tototec.sbuild.Path
import de.tototec.sbuild.Plugin
import de.tototec.sbuild.Project
import de.tototec.sbuild.Target
import de.tototec.sbuild.TargetRef.fromString
import de.tototec.sbuild.addons.support.ForkSupport
import de.tototec.sbuild.toRichFile

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

      val jbakeCp = jbake.jbakeVersion.classpath
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