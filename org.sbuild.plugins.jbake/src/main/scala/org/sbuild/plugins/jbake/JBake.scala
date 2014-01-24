package org.sbuild.plugins.jbake

import java.io.File

/**
 * Configuration for the JBake SBuild Plugin.
 *
 * Based on it's configuration, this plugin will register various targets.
 *
 * @param bakeTargetName Name of the target that will generate (aka 'bake') the site.
 * @param serveTargetName Name of the target that will run an embedded webserver to test the generated site.
 * @param sourceDir Directory containing the source files of the site.
 * @param targetDir Directory, where all generated files will be stored.
 * @param jbakeVersion The JBake version used.
 *   See `[[JBakeVersion]]` for details.
 */
case class JBake(
  bakeTargetName: String,
  serveTargetName: String,
  sourceDir: File,
  targetDir: File,
  jbakeVersion: JBakeVersion = JBakeVersion.JBake_2_2_1)

