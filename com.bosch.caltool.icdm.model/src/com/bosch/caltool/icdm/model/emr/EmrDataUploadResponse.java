/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TRL1COB
 */
public class EmrDataUploadResponse extends EMRFileUploadResponse {

  private Map<Long, EmrPidcVariant> emrPidcVariantMap = new HashMap<>();


  /**
   * @return the emrPidcVariantMap
   */
  public Map<Long, EmrPidcVariant> getEmrPidcVariantMap() {
    return this.emrPidcVariantMap;
  }


  /**
   * @param emrPidcVariantMap the emrPidcVariantMap to set
   */
  public void setEmrPidcVariantMap(final Map<Long, EmrPidcVariant> emrPidcVariantMap) {
    this.emrPidcVariantMap = emrPidcVariantMap;
  }


}
