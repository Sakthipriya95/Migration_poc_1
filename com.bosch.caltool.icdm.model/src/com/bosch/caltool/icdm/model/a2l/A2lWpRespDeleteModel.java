/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashSet;
import java.util.Set;

/**
 * @author pdh2cob
 */
public class A2lWpRespDeleteModel extends A2lWpParamMappingUpdateModel {


  private Set<A2lWpResponsibility> deletedA2lWpResponsibilitySet = new HashSet<>();


  /**
   * @return the deletedA2lWpResponsibilitySet
   */
  public Set<A2lWpResponsibility> getDeletedA2lWpResponsibilitySet() {
    return this.deletedA2lWpResponsibilitySet;
  }


  /**
   * @param deletedA2lWpResponsibilitySet the deletedA2lWpResponsibilitySet to set
   */
  public void setDeletedA2lWpResponsibilitySet(final Set<A2lWpResponsibility> deletedA2lWpResponsibilitySet) {
    this.deletedA2lWpResponsibilitySet = deletedA2lWpResponsibilitySet;
  }


}
