/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmo5cob
 */
public class PredefinedAttrValuesCreationModel {


  private Set<PredefinedAttrValue> valuesToBeCreated = new HashSet<>();

  private Set<PredefinedAttrValue> valuesToBeDeleted = new HashSet<>();


  /**
   * @return the valuesToBeCreated
   */
  public Set<PredefinedAttrValue> getValuesToBeCreated() {
    return this.valuesToBeCreated;
  }


  /**
   * @param valuesToBeCreated the valuesToBeCreated to set
   */
  public void setValuesToBeCreated(final Set<PredefinedAttrValue> valuesToBeCreated) {
    this.valuesToBeCreated = valuesToBeCreated == null ? null : new HashSet<>(valuesToBeCreated);
  }


  /**
   * @return the valuesToBeDeleted
   */
  public Set<PredefinedAttrValue> getValuesToBeDeleted() {
    return this.valuesToBeDeleted;
  }


  /**
   * @param valuesToBeDeleted the valuesToBeDeleted to set
   */
  public void setValuesToBeDeleted(final Set<PredefinedAttrValue> valuesToBeDeleted) {
    this.valuesToBeDeleted = valuesToBeDeleted == null ? null : new HashSet<>(valuesToBeDeleted);
  }


}
