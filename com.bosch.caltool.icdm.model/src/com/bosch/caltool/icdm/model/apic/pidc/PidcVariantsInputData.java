/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dmr1cob
 */
public class PidcVariantsInputData {

  private PidcVersion pidcVersion;

  private Map<Long, PidcVariant> pidcVariantMap = new HashMap<>();

  private boolean isVariantAvailable = true;


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @return the pidcVariantMap
   */
  public Map<Long, PidcVariant> getPidcVariantMap() {
    return this.pidcVariantMap;
  }

  /**
   * @param pidcVariantMap the pidcVariantMap to set
   */
  public void setPidcVariantMap(final Map<Long, PidcVariant> pidcVariantMap) {
    this.pidcVariantMap = pidcVariantMap;
  }


  /**
   * @return the isVariantAvailable
   */
  public boolean isVariantAvailable() {
    return this.isVariantAvailable;
  }


  /**
   * @param isVariantAvailable the isVariantAvailable to set
   */
  public void setVariantAvailable(final boolean isVariantAvailable) {
    this.isVariantAvailable = isVariantAvailable;
  }


}
