import de.tototec.sbuild._
import de.tototec.sbuild.ant._
import de.tototec.sbuild.ant.tasks._

import de.tototec.sbuild._

@version("0.7.0")
@classpath("../../sbuild-plugin/org.sbuild.plugins.sbuildplugin/target/org.sbuild.plugins.sbuildplugin-0.1.0.jar")
class SBuild(implicit _project: Project) {

  Plugin[org.sbuild.plugins.sbuildplugin.SBuildPlugin] configure { c =>
    import c._
    // Note. We require at least 0.7.0.9000 from the immutable-plugin-config branch, as we use an immutable config class.
    sbuildVersion = "0.7.0"

    pluginClass = "org.sbuild.plugins.jbake.JBake"
    pluginVersion = "0.0.9000"
    pluginFactoryClass = "org.sbuild.plugins.jbake.JBakePlugin"
  }

}
