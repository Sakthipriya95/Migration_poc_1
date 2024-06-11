/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.user.User;

/**
 * @author mkl2cob
 */
public class FocusMatrixDetailsModel {

  /**
   * key - focus matrix id , value - object
   */
  private Map<Long, FocusMatrix> focusMatrixMap = new HashMap<>();
  /**
   * set of UcpAttr
   */
  private Set<UcpAttr> ucpAttrSet = new HashSet<>();

  /**
   * User who has reviewed the version
   */
  private User reviewedUser;

  /**
   * @param focusMatrixMap the focusMatrixMap to set
   */
  public void setFocusMatrixMap(final Map<Long, FocusMatrix> focusMatrixMap) {
    this.focusMatrixMap = focusMatrixMap;
  }

  /**
   * @param ucpAttrSet the ucpAttrSet to set
   */
  public void setUcpAttrSet(final Set<UcpAttr> ucpAttrSet) {
    if (ucpAttrSet != null) {
      this.ucpAttrSet = new HashSet<>(ucpAttrSet);
    }
  }

  /**
   * @return the focusMatrixMap
   */
  public Map<Long, FocusMatrix> getFocusMatrixMap() {
    return this.focusMatrixMap;
  }

  /**
   * @return the ucpAttrSet
   */
  public Set<UcpAttr> getUcpAttrSet() {
    return this.ucpAttrSet;
  }

  /**
   * @return the reviewedUser
   */
  public User getReviewedUser() {
    return this.reviewedUser;
  }

  /**
   * @param reviewedUser the reviewedUser to set
   */
  public void setReviewedUser(final User reviewedUser) {
    this.reviewedUser = reviewedUser;
  }

}
