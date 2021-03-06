package com.kyleu.projectile.services.project

import com.kyleu.projectile.models.feature.{EnumFeature, ModelFeature, ServiceFeature}
import com.kyleu.projectile.models.input.Input
import com.kyleu.projectile.models.project.Project
import com.kyleu.projectile.models.project.member.{EnumMember, ModelMember, ServiceMember, UnionMember}
import com.kyleu.projectile.services.ProjectileService

object ProjectUpdateService {
  private[this] val saveUnchanged = false

  def update(svc: ProjectileService, p: Project) = updateInput(svc, p, p.getInput)

  def updateInput(svc: ProjectileService, p: Project, i: Input) = {
    val enumResults = processEnums(svc, p, i)
    val modelResults = processModels(svc, p, i)
    val unionResults = processUnions(svc, p, i)
    val serviceResults = processServices(svc, p, i)

    val results = enumResults ++ modelResults ++ unionResults ++ serviceResults

    val hash = svc.getInput(i.key).hash
    svc.setProjectHash(p.key, hash)

    if (results.isEmpty) {
      Nil
    } else {
      s"Updated project [${p.key}] with hashcode [$hash]:" +: results
    }
  }

  private[this] def processEnums(svc: ProjectileService, p: Project, i: Input) = {
    val (unchanged, enumsToAdd) = i.enums.partition(ek => p.enums.exists(_.key == ek.key))
    val enumsToRemove = p.enums.filterNot(ek => i.enums.exists(_.key == ek.key))
    val ef = p.defaultEnumFeatures.map(EnumFeature.withValue)

    if (saveUnchanged) { unchanged.map(e => svc.saveEnumMember(p.key, p.getEnum(e.key))) }

    enumsToAdd.map { e =>
      svc.saveEnumMember(p.key, EnumMember(pkg = e.pkg, key = e.key, features = ef))
      s"Added enum [${e.key}]"
    } ++ enumsToRemove.map { e =>
      svc.removeEnumMember(p.key, e.key)
      s"Removed enum [${e.key}]"
    }
  }

  private[this] def processModels(svc: ProjectileService, p: Project, i: Input) = {
    val (unchanged, modelsToAdd) = i.models.partition(ek => p.models.exists(_.key == ek.key))
    val modelsToRemove = p.models.filterNot(mk => i.models.exists(_.key == mk.key))
    val mf = p.defaultModelFeatures.map(ModelFeature.withValue)

    if (saveUnchanged) { unchanged.map(m => svc.saveModelMember(p.key, p.getModel(m.key))) }

    modelsToAdd.map { m =>
      svc.saveModelMember(p.key, ModelMember(pkg = m.pkg, key = m.key, features = mf))
      s"Added model [${m.key}]"
    } ++ modelsToRemove.map { m =>
      svc.removeModelMember(p.key, m.key)
      s"Removed model [${m.key}]"
    }
  }

  private[this] def processUnions(svc: ProjectileService, p: Project, i: Input) = {
    val (unchanged, unionsToAdd) = i.unions.partition(ek => p.unions.exists(_.key == ek.key))
    val unionsToRemove = p.unions.filterNot(uk => i.unions.exists(_.key == uk.key))

    if (saveUnchanged) { unchanged.map(m => svc.saveUnionMember(p.key, p.getUnion(m.key))) }

    unionsToAdd.map { u =>
      svc.saveUnionMember(p.key, UnionMember(pkg = u.pkg, key = u.key))
      s"Added union [${u.key}]"
    } ++ unionsToRemove.map { u =>
      svc.removeUnionMember(p.key, u.key)
      s"Removed union [${u.key}]"
    }
  }

  private[this] def processServices(svc: ProjectileService, p: Project, i: Input) = {
    val (unchanged, servicesToAdd) = i.services.partition(sk => p.services.exists(_.key == sk.key))
    val servicesToRemove = p.services.filterNot(sk => i.services.exists(_.key == sk.key))
    val sf = p.defaultServiceFeatures.map(ServiceFeature.withValue)

    if (saveUnchanged) { unchanged.map(s => svc.saveServiceMember(p.key, p.getService(s.key))) }

    servicesToAdd.map { s =>
      svc.saveServiceMember(p.key, ServiceMember(pkg = s.pkg, key = s.key, features = sf))
      s"Added service [${s.key}]"
    } ++ servicesToRemove.map { s =>
      svc.removeServiceMember(p.key, s.key)
      s"Removed service [${s.key}]"
    }
  }
}
