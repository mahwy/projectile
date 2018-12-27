package com.kyleu.projectile.services.project.audit

import better.files._
import com.kyleu.projectile.models.output.OutputPath
import com.kyleu.projectile.models.project.ProjectOutput

object ExportValidation {
  def validate(projectRoot: File, results: Seq[ProjectOutput]) = {
    val out = results.flatMap { result =>
      result.files.map(f => result.getDirectory(result.getDirectory(projectRoot, OutputPath.Root), f.path) / f.filePath)
    }

    val roots = results.map(_.getDirectory(projectRoot, OutputPath.Root)).distinct
    val files = roots.flatMap(root => getGeneratedFiles(root).distinct.map { f =>
      f -> root.relativize(f.path).toString.stripPrefix("/")
    })

    files.flatMap(f => if (out.contains(f._1)) { None } else { Some(f._2 -> "Untracked") })
  }

  private[this] val magicWord = "Generated File"
  private[this] val badBoys = Set("target", "public", ".idea", ".git", "project", "charts", "node_modules")
  private[this] val extensions = Set("graphql", "html", "json", "md", "routes", "scala", "thrift", "txt").map("." + _)

  private[this] def getGeneratedFiles(f: File): Seq[File] = {
    if (!f.isDirectory) { throw new IllegalStateException(s"[$f] is not a directory.") }
    f.children.toSeq.flatMap {
      case child if badBoys(child.name) => Nil
      case child if child.isDirectory => getGeneratedFiles(child)
      case child if extensions.exists(child.name.endsWith) => if (child.contentAsString.contains(magicWord)) {
        Seq(child)
      } else {
        Nil
      }
      case _ => Nil
    }
  }
}
