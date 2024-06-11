/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import java.util.Map;

import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.wp.WpmlWpMasterlist;

/**
 * @author UKT1COB
 */
public class PidcCocWpExternalAPIData extends PidcVersCocWpData {

  /**
   * Map with all WP Master List data for WP mapped for pidc version. <Key - WP Div id , Value - WpmlMasterList>
   */
  private Map<Long, WpmlWpMasterlist> wpmlMasterListMap;
  /**
   * Map with all variants under pidc version. <Key - variant id , Value - PidcVariant>
   */
  private Map<Long, PidcVariant> projectVariantMap;
  /**
   * Map with all sub-variants under pidc version. <Key - sub-variant id , Value - PidcSubVariant>
   */
  private Map<Long, PidcSubVariant> projectSubVariantMap;
  /**
   * PIDC Version
   */
  private PidcVersion pidcVersion;


  /**
   * @return the wpmlMasterListMap
   */
  public Map<Long, WpmlWpMasterlist> getWpmlMasterListMap() {
    return this.wpmlMasterListMap;
  }


  /**
   * @param wpmlMasterListMap the wpmlMasterListMap to set
   */
  public void setWpmlMasterListMap(final Map<Long, WpmlWpMasterlist> wpmlMasterListMap) {
    this.wpmlMasterListMap = wpmlMasterListMap;
  }


  /**
   * @return the projectVariantMap
   */
  public Map<Long, PidcVariant> getProjectVariantMap() {
    return this.projectVariantMap;
  }


  /**
   * @param projectVariantMap the projectVariantMap to set
   */
  public void setProjectVariantMap(final Map<Long, PidcVariant> projectVariantMap) {
    this.projectVariantMap = projectVariantMap;
  }


  /**
   * @return the projectSubVariantMap
   */
  public Map<Long, PidcSubVariant> getProjectSubVariantMap() {
    return this.projectSubVariantMap;
  }


  /**
   * @param projectSubVariantMap the projectSubVariantMap to set
   */
  public void setProjectSubVariantMap(final Map<Long, PidcSubVariant> projectSubVariantMap) {
    this.projectSubVariantMap = projectSubVariantMap;
  }


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

}
