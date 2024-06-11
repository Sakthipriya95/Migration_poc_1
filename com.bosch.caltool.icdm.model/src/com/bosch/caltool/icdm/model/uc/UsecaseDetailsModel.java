/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmo5cob
 */
public class UsecaseDetailsModel extends UsecaseTreeGroupModel {

  private Map<Long, UseCaseSection> ucSectionMap = new HashMap<>();
  /**
   * key - use case id, value - UsecaseEditorModel
   */
  private Map<Long, UsecaseModel> usecaseDetailsModelMap = new HashMap<>();

  /**
   * @return the usecaseDetailsModelSet
   */
  public Map<Long, UsecaseModel> getUsecaseDetailsModelMap() {
    return this.usecaseDetailsModelMap;
  }

  /**
   * @return the ucSectionMap
   */
  public Map<Long, UseCaseSection> getUcSectionMap() {
    return this.ucSectionMap;
  }


  /**
   * @param ucSectionMap the ucSectionMap to set
   */
  public void setUcSectionMap(final Map<Long, UseCaseSection> ucSectionMap) {
    this.ucSectionMap = ucSectionMap;
  }


  /**
   * @param usecaseDetailsModelMap the usecaseDetailsModelMap to set
   */
  public void setUsecaseDetailsModelMap(final Map<Long, UsecaseModel> usecaseDetailsModelMap) {
    this.usecaseDetailsModelMap = usecaseDetailsModelMap;
  }

}
