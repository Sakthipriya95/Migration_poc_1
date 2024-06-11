/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.List;

/**
 * @author OZY1KOR
 */
public class A2lWpRespResetModel {

  private A2lWpRespDeleteModel a2lWpRespDeleteModel;

  private A2lWpDefnVersion newA2lWpDefnVersion;

  private List<A2lVariantGroup> deletedA2lVariantGroups;

  /**
   * @return the a2lWpRespDeleteModel
   */
  public A2lWpRespDeleteModel getA2lWpRespDeleteModel() {
    return this.a2lWpRespDeleteModel;
  }

  /**
   * @param a2lWpRespDeleteModel the a2lWpRespDeleteModel to set
   */
  public void setA2lWpRespDeleteModel(final A2lWpRespDeleteModel a2lWpRespDeleteModel) {
    this.a2lWpRespDeleteModel = a2lWpRespDeleteModel;
  }

  /**
   * @return the newA2lWpDefnVersion
   */
  public A2lWpDefnVersion getNewA2lWpDefnVersion() {
    return this.newA2lWpDefnVersion;
  }

  /**
   * @param newA2lWpDefnVersion the newA2lWpDefnVersion to set
   */
  public void setNewA2lWpDefnVersion(final A2lWpDefnVersion newA2lWpDefnVersion) {
    this.newA2lWpDefnVersion = newA2lWpDefnVersion;
  }


  /**
   * @return the deletedA2lVariantGroup
   */
  public List<A2lVariantGroup> getDeletedA2lVariantGroup() {
    return this.deletedA2lVariantGroups;
  }


  /**
   * @param deletedA2lVariantGroup the deletedA2lVariantGroup to set
   */
  public void setDeletedA2lVariantGroup(final List<A2lVariantGroup> deletedA2lVariantGroup) {
    this.deletedA2lVariantGroups = deletedA2lVariantGroup;
  }

}