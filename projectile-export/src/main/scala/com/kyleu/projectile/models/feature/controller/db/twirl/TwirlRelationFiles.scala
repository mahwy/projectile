package com.kyleu.projectile.models.feature.controller.db.twirl

import com.kyleu.projectile.models.export.{ExportField, ExportModel}
import com.kyleu.projectile.models.export.config.ExportConfiguration
import com.kyleu.projectile.models.output.CommonImportHelper
import com.kyleu.projectile.models.output.file.TwirlFile

object TwirlRelationFiles {
  private[this] val viewArgs = "orderBy: Option[String], orderAsc: Boolean, limit: Int, offset: Int"

  private[this] def writeTable(config: ExportConfiguration, model: ExportModel, refFields: Seq[ExportField], listFile: TwirlFile) = {
    val viewCall = TwirlHelper.routesClass(config, model) + ".by" + refFields.map(_.className).mkString
    val refProps = refFields.map(_.propertyName).mkString(", ")
    val refArgs = refFields.map(r => r.propertyName + ": " + r.scalaTypeFull(config).mkString(".")).mkString(", ")

    val modelPkg = (config.applicationPackage :+ "models").mkString(".")
    val listCalls = (config.systemPackage ++ Seq("models", "result", "web", "ListCalls")).mkString(".")

    val su = CommonImportHelper.getString(config, "SystemUser")
    val aa = CommonImportHelper.getString(config, "AuthActions")
    listFile.add(s"@(user: $su, authActions: $aa, $refArgs, modelSeq: Seq[${model.fullClassPath(config)}], $viewArgs)(", 2)
    val td = config.utilitiesPackage.mkString(".") + ".tracing.TraceData"
    listFile.add(s"implicit request: Request[AnyContent], session: Session, flash: Flash, traceData: $td")
    listFile.add(")", -2)

    listFile.add(s"@${(config.systemViewPackage :+ "html").mkString(".")}.admin.explore.list(", 1)
    listFile.add("user = user,")
    listFile.add("authActions = authActions,")
    listFile.add(s"""model = "${model.title}",""")
    listFile.add(s"""modelPlural = "${model.plural}",""")
    listFile.add(s"icon = $modelPkg.template.Icons.${model.propertyName},")
    listFile.add("cols = Seq(", 1)
    model.searchFields.foreach {
      case c if model.searchFields.lastOption.contains(c) => listFile.add(s""""${c.propertyName}" -> "${c.title}"""")
      case c => listFile.add(s""""${c.propertyName}" -> "${c.title}",""")
    }
    listFile.add("),", -1)
    listFile.add("orderBy = orderBy,")
    listFile.add("orderAsc = orderAsc,")
    listFile.add("totalCount = None,")
    listFile.add(s"rows = modelSeq.map(model => ${model.viewHtmlPackage(config).mkString(".")}.${model.propertyName}DataRow(model)),")
    listFile.add(s"calls = $listCalls(", 1)
    listFile.add(s"orderBy = Some($viewCall($refProps, _, _, Some(limit), Some(0))),")
    listFile.add("search = None,")
    listFile.add(s"next = $viewCall($refProps, orderBy, orderAsc, Some(limit), Some(offset + limit)),")
    listFile.add(s"prev = $viewCall($refProps, orderBy, orderAsc, Some(limit), Some(offset - limit)),")
    listFile.add("),", -1)
    listFile.add("limit = limit,")
    listFile.add("offset = offset,")
    listFile.add("q = None,")
    listFile.add("fullUI = false")
    listFile.add(")", -1)

  }

  def export(config: ExportConfiguration, model: ExportModel) = {
    model.foreignKeys.flatMap {
      case fk if fk.references.lengthCompare(1) == 0 =>
        val refFields = fk.references.map(r => model.getField(r.source))
        val listFile = TwirlFile(model.viewPackage(config), model.propertyName + "By" + refFields.map(_.className).mkString)
        writeTable(config, model, refFields, listFile)
        Some(listFile)
      case _ => None
    }
  }
}
