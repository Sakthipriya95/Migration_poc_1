/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.attr;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dmo5cob
 */
public class PredefinedValidityCreationModel {

  /**
   * Set of Validity attribute values selected from the AddValidityAttrValDialog
   */
  private Set<PredefinedValidity> validityToBeCreated = new HashSet<>();
  /**
   * Set of Validity attribute values to be deleted
   */
  private Set<PredefinedValidity> validityToBeDeleted = new HashSet<>();

  /**
   * @return the validityToBeCreated
   */
  public Set<PredefinedValidity> getValidityToBeCreated() {
    return this.validityToBeCreated;
  }

  /**
   * @param validityToBeCreated the validityToBeCreated to set
   */
  public void setValidityToBeCreated(final Set<PredefinedValidity> validityToBeCreated) {
    this.validityToBeCreated = validityToBeCreated == null ? null : new HashSet<>(validityToBeCreated);
  }

  /**
   * @return the validityToBeDeleted
   */
  public Set<PredefinedValidity> getValidityToBeDeleted() {
    return this.validityToBeDeleted;
  }

  /**
   * @param validityToBeDeleted the validityToBeDeleted to set
   */
  public void setValidityToBeDeleted(final Set<PredefinedValidity> validityToBeDeleted) {
    this.validityToBeDeleted = validityToBeDeleted == null ? null : new HashSet<>(validityToBeDeleted);
  }
}
