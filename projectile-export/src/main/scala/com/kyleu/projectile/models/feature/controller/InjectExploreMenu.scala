package com.kyleu.projectile.models.feature.controller

import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.feature.controller.db.twirl.TwirlHelper
import com.kyleu.projectile.models.feature.{FeatureLogic, ModelFeature}
import com.kyleu.projectile.models.output.OutputPath
import com.kyleu.projectile.models.output.inject.{CommentProvider, TextSectionHelper}

object InjectExploreMenu extends FeatureLogic.Inject(path = OutputPath.ServerSource, filename = "menu.scala.html") {
  override def dir(config: ExportConfiguration) = config.viewPackage :+ "admin" :+ "layout"

  private[this] def modelsFor(config: ExportConfiguration) = {
    val filtered = config.models.filter(_.features(ModelFeature.Controller)).filter(_.inputType.isDatabase)
    val roots = filtered.filter(_.pkg.isEmpty).sortBy(_.title)
    val pkgGroups = filtered.filterNot(_.pkg.isEmpty).groupBy(_.pkg.headOption.getOrElse(
      throw new IllegalStateException()
    )).mapValues(_.sortBy(_.title)).toSeq.sortBy(_._1)
    roots -> pkgGroups
  }

  override def logic(config: ExportConfiguration, markers: Map[String, Seq[String]], original: Seq[String]) = {
    val (roots, pkgGroups) = modelsFor(config)

    val newLines = (roots ++ pkgGroups.flatMap(_._2)).sortBy(_.title).map { model =>
      s"""<li><a href="@${TwirlHelper.routesClass(config, model)}.list()">${TwirlHelper.iconHtml(config, model.propertyName)} ${model.title}</a></li>"""
    }

    val params = TextSectionHelper.Params(commentProvider = CommentProvider.Twirl, key = "model list routes")
    TextSectionHelper.replaceBetween(filename = filename, original = original, p = params, newLines = newLines, project = config.project.key)
  }
}
