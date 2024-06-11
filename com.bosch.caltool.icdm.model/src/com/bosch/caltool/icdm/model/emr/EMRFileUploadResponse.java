/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mkl2cob
 */
public class EMRFileUploadResponse {

  /**
   * key- emr file id, value - error list
   */
  private Map<Long, List<EmrUploadError>> emrFileErrorMap = new HashMap<>();

  /**
   * key - emr file id, value - EmrFile
   */
  private Map<Long, EmrFile> emrFileMap = new HashMap<>();

  /**
   * @return the emrFileErrorMap
   */
  public Map<Long, List<EmrUploadError>> getEmrFileErrorMap() {
    return this.emrFileErrorMap;
  }


  /**
   * @param emrFileErrorMap the emrFileErrorMap to set
   */
  public void setEmrFileErrorMap(final Map<Long, List<EmrUploadError>> emrFileErrorMap) {
    this.emrFileErrorMap = emrFileErrorMap;
  }


  /**
   * @return the emrFileMap
   */
  public Map<Long, EmrFile> getEmrFileMap() {
    return this.emrFileMap;
  }


  /**
   * @param emrFileMap the emrFileMap to set
   */
  public void setEmrFileMap(final Map<Long, EmrFile> emrFileMap) {
    this.emrFileMap = emrFileMap;
  }


}
