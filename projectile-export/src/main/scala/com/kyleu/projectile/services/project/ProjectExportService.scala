package com.kyleu.projectile.services.project

import better.files.File
import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.feature.FeatureService
import com.kyleu.projectile.models.output.OutputLog
import com.kyleu.projectile.models.project.ProjectOutput
import com.kyleu.projectile.services.ProjectileService

class ProjectExportService(val projectile: ProjectileService) {
  def getOutput(projectRoot: File, key: String, verbose: Boolean) = runExport(projectRoot, projectile.loadConfig(key), verbose)

  def runExport(projectRoot: File, config: ExportConfiguration, verbose: Boolean) = {
    val startMs = System.currentTimeMillis

    val rootLogs = if (verbose) {
      Seq(OutputLog(s"Project Export (${config.project.features.size} features, ${config.enums.size} enums, ${config.models.size} models)", 0L))
    } else {
      Nil
    }

    val featureOutputs = config.project.features.map(feature => FeatureService.export(feature, projectRoot, config, verbose)).toSeq

    ProjectOutput(project = config.project.toSummary, rootLogs = rootLogs, featureOutput = featureOutputs, duration = System.currentTimeMillis - startMs)
  }
}
