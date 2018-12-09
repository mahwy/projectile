package com.projectile.models.thrift.input

import com.projectile.models.export._
import com.projectile.models.input.InputType
import com.projectile.models.output.ExportHelper
import com.projectile.models.output.ExportHelper.toClassName
import com.projectile.models.thrift.schema.{ThriftService, ThriftStructField}

object ThriftExportService {
  def loadService(s: ThriftService, input: ThriftInput) = ExportService(
    inputType = InputType.Service.ThriftService,
    pkg = s.pkg.toList :+ "services",
    key = s.key,
    className = toClassName(s.key),
    methods = loadServiceMethods(s, input),
    features = Set.empty
  )

  private[this] def loadArguments(args: Seq[ThriftStructField], input: ThriftInput) = args.map { arg =>
    val t = ThriftFileHelper.columnTypeFor(arg.t, input)
    ExportField(
      key = arg.key,
      propertyName = ExportHelper.toIdentifier(arg.name),
      title = ExportHelper.toDefaultTitle(arg.key),
      description = None,
      t = t,
      defaultValue = arg.value.map(_.toString),
      required = arg.required
    )
  }.toList

  private[this] def loadServiceMethods(s: ThriftService, input: ThriftInput) = s.methods.map { m =>
    val args = loadArguments(m.arguments, input)
    val returnType = ThriftFileHelper.columnTypeFor(m.returnType, input)
    ExportMethod(key = m.key, args = args, returnType = returnType)
  }.toList
}
