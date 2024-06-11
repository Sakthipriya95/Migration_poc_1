/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Response for service to fetch EMR sheet data
 *
 * @author dja7cob
 */
public class EmrDataExternalResponse {

  /**
   * Map of key - Emr File ID; Value - Emr FileDetails
   */
  private Map<Long, Set<EmrFileDetails>> emrFileDetailsMap = new HashMap<>();

  /**
   * Map of key - Emr File ID; Value - Map of Ems Id, Ems Details
   */
  private Map<Long, Map<Long, EmsEmrVariants>> emsEmrVariantsMap = new HashMap<>();

  /**
   * Map of key - Emr File ID; Value - Map of Emr File Data Id, Emr File Contents
   */
  private Map<Long, Map<Long, EmrData>> emrDataMap = new HashMap<>();


  /**
   * @return the emrFileDetailsMap
   */
  public Map<Long, Set<EmrFileDetails>> getEmrFileDetailsMap() {
    return this.emrFileDetailsMap;
  }


  /**
   * @param emrFileDetailsMap the emrFileDetailsMap to set
   */
  public void setEmrFileDetailsMap(final Map<Long, Set<EmrFileDetails>> emrFileDetailsMap) {
    this.emrFileDetailsMap = emrFileDetailsMap;
  }


  /**
   * @return the emrFileDataMap
   */
  public Map<Long, Map<Long, EmrData>> getEmrDataMap() {
    return this.emrDataMap;
  }


  /**
   * @param emrDataMap the emrDataMap to set
   */
  public void setEmrDataMap(final Map<Long, Map<Long, EmrData>> emrDataMap) {
    this.emrDataMap = emrDataMap;
  }


  /**
   * @return the emsDetailsMap
   */
  public Map<Long, Map<Long, EmsEmrVariants>> getEmsEmrVariantsMap() {
    return this.emsEmrVariantsMap;
  }


  /**
   * @param emsEmrVariantsMap the emsEmrVariantsMap to set
   */
  public void setEmsEmrVariantsMap(final Map<Long, Map<Long, EmsEmrVariants>> emsEmrVariantsMap) {
    this.emsEmrVariantsMap = emsEmrVariantsMap;
  }
}
