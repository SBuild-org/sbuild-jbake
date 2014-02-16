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
      targetDir = Path("target") / (if (name == "") "jbake" else s"site-${name}"),
      initTargetName = if (name == "") "init-jbake" else s"init-jbake-${name}"
    )
    jbake
  }

  override def applyToProject(instances: Seq[(String, JBake)]): Unit = instances foreach {
    case (name, jbake) =>

      val jbakeCp = jbake.jbakeVersion.classpath
      val sourceFiles = s"scan:${jbake.sourceDir.getPath}"

      // TODO: make bake cacheable

      Target(s"phony:${jbake.bakeTargetName}") dependsOn jbakeCp ~ sourceFiles exec {
        jbake.targetDir.mkdirs
        val retVal = ForkSupport.runJavaAndWait(
          classpath = jbakeCp.files,
          arguments = Array("org.jbake.launcher.Main", jbake.sourceDir.getPath, jbake.targetDir.getPath),
          failOnError = true
        )
        if(retVal != 0) throw new RuntimeException(s"jbake returned with exit code ${retVal}")
      }

      Target(s"phony:${jbake.serveTargetName}") dependsOn jbakeCp ~ jbake.bakeTargetName exec {
        val retVal = ForkSupport.runJavaAndWait(
          classpath = jbakeCp.files,
          arguments = Array("org.jbake.launcher.Main", "-s", jbake.targetDir.getPath),
          failOnError = true
        )
        if(retVal != 0) throw new RuntimeException(s"jbake returned with exit code ${retVal}")
      }

      jbake.jbakeVersion.baseZip match {
        case None => // no init possible
        case Some(baseZip) =>
          Target(s"phony:${jbake.initTargetName}") dependsOn baseZip ~ sourceFiles exec {
            if (!sourceFiles.files.isEmpty) {
              throw new RuntimeException(s"Source directory ${jbake.sourceDir} is not empty. Abort initializing a fresh JBake project layout.")
            }
            de.tototec.sbuild.internal.Util.unzip(baseZip.files.head, jbake.sourceDir)
          }

      }

  }
}