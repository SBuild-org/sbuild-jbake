package org.sbuild.plugins.jbake

import java.io.File

/**
 * Configuration for the JBake SBuild Plugin.
 *
 * @param bakeTargetName Name of the target that will generate (aka 'bake') the site.
 * @param serveTargetName Name of the target that will run an embedded webserver to test the generated site.
 * @param sourceDir Directory containing the source files of the site.
 * @param targetDir Directory, where all generated files will be stored.
 * @param jbakeHome Directory of an locally JBake installation.
 *   If `[[scala.None$]]`, then no local installation will be used.
 *   Instead, a version specified by the `jbakeVersion` property will be downloaded and used.
 * @param jbakeVersion The JBake version automatically downloaded and used to generate the site.
 */
case class JBake(
  bakeTargetName: String,
  serveTargetName: String,
  sourceDir: File,
  targetDir: File,
  jbakeHome: Option[File] = None,
  jbakeVersion: String = "2.2.0")