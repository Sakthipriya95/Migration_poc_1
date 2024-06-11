/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.List;

/**
 * @author bru2cob
 */
public class ImportParamCommentData {


  private List<Long> paramList;

  private Long sourceResultId;

  private boolean overWriteComments;

  private boolean includeScore;


  /**
   * @return the includeScore
   */
  public boolean isIncludeScore() {
    return this.includeScore;
  }


  /**
   * @param includeScore the includeScore to set
   */
  public void setIncludeScore(final boolean includeScore) {
    this.includeScore = includeScore;
  }


  /**
   * @return the sourceResultId
   */
  public Long getSourceResultId() {
    return this.sourceResultId;
  }


  /**
   * @param sourceResultId the sourceResultId to set
   */
  public void setSourceResultId(final Long sourceResultId) {
    this.sourceResultId = sourceResultId;
  }


  /**
   * @return the paramList
   */
  public List<Long> getParamList() {
    return this.paramList;
  }


  /**
   * @param paramList the paramList to set
   */
  public void setParamList(final List<Long> paramList) {
    this.paramList = paramList;
  }


  /**
   * @return the overWriteComments
   */
  public boolean isOverWriteComments() {
    return this.overWriteComments;
  }

  /**
   * @param overWriteComments the overWriteComments to set
   */
  public void setOverWriteComments(final boolean overWriteComments) {
    this.overWriteComments = overWriteComments;
  }


}
