/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author DJA7COB
 */
public class RvwQnaireResponseLink {

  /**
   * Map to hold qnaires which are linked primarily
   */
  Map<Long, Map<Long, Set<Long>>> primaryQnaireWpRespLinkMap = new HashMap<>();

  /**
   * Map to hold qnaires which are linked secondarily
   */
  Map<Long, Map<Long, Set<Long>>> secondaryQnaireWpRespLinkMap = new HashMap<>();


  /**
   * @return the primaryQnaireWpRespLinkMap
   */
  public Map<Long, Map<Long, Set<Long>>> getPrimaryQnaireWpRespLinkMap() {
    return this.primaryQnaireWpRespLinkMap;
  }


  /**
   * @param primaryQnaireWpRespLinkMap the primaryQnaireWpRespLinkMap to set
   */
  public void setPrimaryQnaireWpRespLinkMap(final Map<Long, Map<Long, Set<Long>>> primaryQnaireWpRespLinkMap) {
    this.primaryQnaireWpRespLinkMap = primaryQnaireWpRespLinkMap;
  }


  /**
   * @return the secondaryQnaireWpRespLinkMap
   */
  public Map<Long, Map<Long, Set<Long>>> getSecondaryQnaireWpRespLinkMap() {
    return this.secondaryQnaireWpRespLinkMap;
  }


  /**
   * @param secondaryQnaireWpRespLinkMap the secondaryQnaireWpRespLinkMap to set
   */
  public void setSecondaryQnaireWpRespLinkMap(final Map<Long, Map<Long, Set<Long>>> secondaryQnaireWpRespLinkMap) {
    this.secondaryQnaireWpRespLinkMap = secondaryQnaireWpRespLinkMap;
  }


}
