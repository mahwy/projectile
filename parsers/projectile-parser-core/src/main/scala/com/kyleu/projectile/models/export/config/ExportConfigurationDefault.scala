package com.kyleu.projectile.models.export.config

import com.kyleu.projectile.models.database.schema.Column
import com.kyleu.projectile.models.export.{ExportEnum, ExportField}
import com.kyleu.projectile.models.output.ExportHelper.{toDefaultTitle, toIdentifier}

object ExportConfigurationDefault {
  private[this] def clean(str: String) = str match {
    case "type" => "typ"
    case _ => str
  }

  def loadField(col: Column, indexed: Boolean, unique: Boolean, inSearch: Boolean = false, enums: Seq[ExportEnum]) = ExportField(
    key = col.name,
    propertyName = clean(toIdentifier(col.name)),
    title = toDefaultTitle(col.name),
    description = col.description,
    t = col.columnType,
    defaultValue = col.defaultValue,
    required = col.notNull,
    indexed = indexed,
    unique = unique,
    inSearch = inSearch,
    inSummary = inSearch
  )
}
