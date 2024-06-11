/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author say8cob
 */
public class RvwQnaireInput {

  private Long pidcVersionId;

  private Long pidcVariantId;

  private String pidcVariantName;

  private Set<String> selectedFunctionSet;


  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }


  /**
   * @param pidcVariantId the pidcVariantId to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }


  /**
   * @return the pidcVariantName
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }


  /**
   * @param pidcVariantName the pidcVariantName to set
   */
  public void setPidcVariantName(final String pidcVariantName) {
    this.pidcVariantName = pidcVariantName;
  }


  /**
   * @return the selectedFunctionSet
   */
  public Set<String> getSelectedFunctionSet() {
    return this.selectedFunctionSet;
  }


  /**
   * @param selectedFunctionSet the selectedFunctionSet to set
   */
  public void setSelectedFunctionSet(final Set<String> selectedFunctionSet) {
    this.selectedFunctionSet = selectedFunctionSet == null ? null : new TreeSet<>(selectedFunctionSet);
  }


}
