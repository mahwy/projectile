package models.output.feature.graphql

import models.export.ExportEnum
import models.export.config.ExportConfiguration
import models.output.OutputPath
import models.output.feature.EnumFeature
import models.output.file.ScalaFile

object EnumSchemaFile {
  def export(config: ExportConfiguration, enum: ExportEnum) = {
    val file = ScalaFile(path = OutputPath.ServerSource, config.applicationPackage ++ enum.modelPackage, enum.className + "Schema")
    file.addImport(config.systemPackage :+ "graphql", "CommonSchema")
    file.addImport(config.applicationPackage :+ "graphql", "GraphQLContext")
    file.addImport(config.applicationPackage :+ "graphql", "GraphQLSchemaHelper")
    file.addImport(Seq("sangria", "schema"), "EnumType")
    file.addImport(Seq("sangria", "schema"), "ListType")
    file.addImport(Seq("sangria", "schema"), "fields")
    file.addImport(Seq("scala", "concurrent"), "Future")

    file.add(s"""object ${enum.className}Schema extends GraphQLSchemaHelper("${enum.propertyName}") {""", 1)
    file.add(s"implicit val ${enum.propertyName}EnumType: EnumType[${enum.className}] = CommonSchema.deriveStringEnumeratumType(", 1)
    file.add(s"""name = "${enum.className}",""")
    file.add(s"values = ${enum.className}.values")
    file.add(")", -1)
    file.add()

    file.add("val queryFields = fields(", 1)
    val r = s"""Future.successful(${enum.className}.values)"""
    file.add(s"""unitField(name = "${enum.propertyName}", desc = None, t = ListType(${enum.propertyName}EnumType), f = (_, _) => $r)""")
    file.add(")", -1)
    file.add("}", -1)

    file
  }
}
