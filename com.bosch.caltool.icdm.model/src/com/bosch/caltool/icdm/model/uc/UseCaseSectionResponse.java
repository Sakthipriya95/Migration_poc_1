/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author and4cob
 */
public class UseCaseSectionResponse {

  private Set<UseCaseSection> ucSectionSet = new HashSet<>();


  /**
   * @return the ucSectionSet
   */
  public Set<UseCaseSection> getUcSectionSet() {
    return this.ucSectionSet;
  }


  /**
   * @param ucSectionSet the ucSectionSet to set
   */
  public void setUcSectionSet(final Set<UseCaseSection> ucSectionSet) {
    this.ucSectionSet = ucSectionSet;
  }

}
