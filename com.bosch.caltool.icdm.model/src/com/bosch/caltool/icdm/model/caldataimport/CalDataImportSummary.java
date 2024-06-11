/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.caldataimport;

import java.util.SortedSet;
import java.util.TreeSet;


/**
 * @author bne4cob
 */
public class CalDataImportSummary {

  /**
   * Parameters for which rules has been created
   */
  private final SortedSet<String> paramRulesCreatedSet = new TreeSet<>();

  /**
   * Parameters for which rules has been updated
   */
  private final SortedSet<String> paramRulesUpdatedSet = new TreeSet<>();
  /**
   * Creation/modification not done
   */
  private final SortedSet<String> paramInvalidSet = new TreeSet<>();
  /**
   * parameters count which are not updated/created
   */
  private int skippedParamsCount;

  /**
   * Total number of parameters available in file
   */
  private int totalNoOfParams;


  /**
   * Import message
   */
  private String message = "No changes to be saved in rules!";

  /**
   * Import status
   */
  private int status;


  /**
   * @return the paramRulesCreatedSet
   */
  public SortedSet<String> getParamRulesCreatedSet() {
    return this.paramRulesCreatedSet;
  }


  /**
   * @return the paramRulesUpdatedSet
   */
  public SortedSet<String> getParamRulesUpdatedSet() {
    return this.paramRulesUpdatedSet;
  }

  /**
   * @return the paramRulesUpdatedSet
   */
  public SortedSet<String> getParamInvalidSet() {
    return this.paramInvalidSet;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return this.message;
  }


  /**
   * @param message the message to set
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /**
   * @return the status
   */
  public int getStatus() {
    return this.status;
  }

  /**
   * @return Rules Duplicate Count
   */
  public int getSkippedParamsCount() {
    return this.skippedParamsCount;
  }

  /**
   * @param duplicateParamsCount the duplicateParamsCount to set
   */
  public void setSkippedParamsCount(final int duplicateParamsCount) {
    this.skippedParamsCount = duplicateParamsCount;
  }

  /**
   * @param size int
   */
  public void setTotalParamsInInput(final int size) {
    this.totalNoOfParams = size;
  }


  /**
   * @return the totalNoOfParams
   */
  public int getTotalNoOfParams() {
    return this.totalNoOfParams;
  }

}
