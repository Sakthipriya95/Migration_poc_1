/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Set;

/**
 * Input for PIDC Scout REST service
 *
 * @author bne4cob
 */
// ICDM-2326
public class PidcSearchInput {

  /**
   * Search conditions
   */
  private Set<PidcSearchCondition> searchConditions;

  private boolean searchFocusMatrix = true;

  private boolean searchA2lFiles = true;

  private boolean searchReviews = true;


  /**
   * Constructor
   */
  public PidcSearchInput() {
    this.searchConditions = new HashSet<>();
  }


  /**
   * @return the searchConditions
   */
  public Set<PidcSearchCondition> getSearchConditions() {
    return this.searchConditions;
  }


  /**
   * @param searchConditions the searchConditions to set
   */
  public void setSearchConditions(final Set<PidcSearchCondition> searchConditions) {
    this.searchConditions = searchConditions == null ? new HashSet<>() : new HashSet<>(searchConditions);
  }

  /**
   * @return the searchFocusMatrix
   */
  public boolean isSearchFocusMatrix() {
    return this.searchFocusMatrix;
  }


  /**
   * @param searchFocusMatrix the searchFocusMatrix to set
   */
  public void setSearchFocusMatrix(final boolean searchFocusMatrix) {
    this.searchFocusMatrix = searchFocusMatrix;
  }


  /**
   * @return the searchA2lFiles
   */
  public boolean isSearchA2lFiles() {
    return this.searchA2lFiles;
  }


  /**
   * @param searchA2lFiles the searchA2lFiles to set
   */
  public void setSearchA2lFiles(final boolean searchA2lFiles) {
    this.searchA2lFiles = searchA2lFiles;
  }


  /**
   * @return the searchReviews
   */
  public boolean isSearchReviews() {
    return this.searchReviews;
  }


  /**
   * @param searchReviews the searchReviews to set
   */
  public void setSearchReviews(final boolean searchReviews) {
    this.searchReviews = searchReviews;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PidcSearchInput [searchConditions=" + this.searchConditions + ", searchFocusMatrix=" +
        this.searchFocusMatrix + ", searchA2lFiles=" + this.searchA2lFiles + ", searchReviews=" + this.searchReviews +
        "]";
  }


}
