package org.sbuild.plugins.jbake

import java.io.File

import de.tototec.sbuild.Project
import de.tototec.sbuild.TargetRefs
import de.tototec.sbuild.TargetRefs.fromString

/**
 * A configuration of a JBake instance.
 * There are three different flavours:
 * - `[[JBakeVersion$.Packaged]]` - A JBake distribution ZIP, defined by `version` and `url` (which can be any `[[TargetRef]]`)
 * - `[[JBakeVersion$.Local]]` -- A locally installed JBake, defined by a `homeDir`.
 * - `[[JBakeVersion$.Classpath]]` -- JBake will completely resolved from the given classpath, which is a `[[TargetRefs]]`.
 *
 * Note, there are also some predefined versions available in `[[JBakeVersion$]]`.
 */
sealed trait JBakeVersion {
  def classpath(implicit p: Project): TargetRefs
}

object JBakeVersion {

  object Packaged {
    /**
     * Conveniently download and use a released version from `jbake.org`.
     */
    def apply(version: String): JBakeVersion = Packaged(version, s"http://jbake.org/files/jbake-${version}-bin.zip")
  }
  /**
   *  A JBake distribution ZIP.
   *
   *  @param version The JBake version this package contains.
   *  @param url The URL to the package. This parameter supports also any [[TargetRef.name]].
   */
  case class Packaged(version: String, url: String) extends JBakeVersion {
    override def classpath(implicit p: Project): TargetRefs = s"zip:file=jbake-${version}/jbake-core.jar;archive=${url}" ~ s"zip:regex=jbake-${version}/lib/.*[.][Jj][Aa][Rr];archive=${url}"
  }
  /**
   * A locally installed (or at least unpacked) JBake.
   *
   * @param homeDir The installation directory of the locally installed JBake version.
   */
  case class Local(homeDir: File) extends JBakeVersion {
    override def classpath(implicit p: Project): TargetRefs = s"${homeDir}/jbake-core.jar" ~ s"scan:${homeDir}/lib;regex=.*\.jar"
  }
  /**
   * The JBake runtime is completely resolved from the given `explicitClasspath`.
   *
   * @param explicitClasspath The classpath, from where the JBake runtime will be loaded.
   */
  case class Classpath(explicitClasspath: TargetRefs) extends JBakeVersion {
    override def classpath(implicit p: Project): TargetRefs = explicitClasspath
  }

  /** JBake 2.2.0 */
  val JBake_2_2_0 = Packaged("2.2.0")

  /** JBake 2.2.1 */
  val JBake_2_2_1 = Packaged("2.2.1")

}
