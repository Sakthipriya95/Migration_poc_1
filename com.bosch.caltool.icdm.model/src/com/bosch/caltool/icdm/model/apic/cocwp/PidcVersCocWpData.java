/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * @author UKT1COB
 */
public class PidcVersCocWpData {

  /**
   * Map with all wrkPkgDiv for the division specific to pidc version. Key - wrkPkg Div Id, Value - WorkPackageDivision
   * model
   */
  private Map<Long, WorkPackageDivision> wrkPkgDivMap;
  /**
   * Map with all CocWP to be displayed virtually for pidc version. Key - WpDivId, Value - PidcVersCocWp, PidcVersCocWp
   * Id will be null if there is no data in TPidcVersCocWp table
   */
  private Map<Long, PidcVersCocWp> pidcVersCocWpMap = new HashMap<>();

  /**
   * Map with all CocWP in pidc version's variant level that is stored in TPidcVariantCocWp table.<Key - Variant id,
   * Value - <Key - WpDivId,Value - PidcVariantCocWp>>
   */
  private Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMap = new HashMap<>();

  /**
   * Map with all CocWP in pidc version's sub variant level that is stored in TPidcSuVarCocWp table. <Key - Sub-variant
   * id, Value - <Key -WpDivId,Value- PidcSubVarCocWp>>
   */
  private Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpMap = new HashMap<>();

  /**
   * @return the wrkPkgDivMap
   */
  public Map<Long, WorkPackageDivision> getWrkPkgDivMap() {
    return this.wrkPkgDivMap;
  }


  /**
   * @param wrkPkgDivMap the wrkPkgDivMap to set
   */
  public void setWrkPkgDivMap(final Map<Long, WorkPackageDivision> wrkPkgDivMap) {
    this.wrkPkgDivMap = wrkPkgDivMap;
  }


  /**
   * @return the pidcVersCocWpMap
   */
  public Map<Long, PidcVersCocWp> getPidcVersCocWpMap() {
    return this.pidcVersCocWpMap;
  }


  /**
   * @param pidcVersCocWpMap the pidcVersCocWpMap to set
   */
  public void setPidcVersCocWpMap(final Map<Long, PidcVersCocWp> pidcVersCocWpMap) {
    this.pidcVersCocWpMap = pidcVersCocWpMap;
  }


  /**
   * @return the pidcVarCocWpMap
   */
  public Map<Long, Map<Long, PidcVariantCocWp>> getPidcVarCocWpMap() {
    return this.pidcVarCocWpMap;
  }


  /**
   * @param definedPidcVarCocWpMap the pidcVarCocWpMap to set
   */
  public void setPidcVarCocWpMap(final Map<Long, Map<Long, PidcVariantCocWp>> definedPidcVarCocWpMap) {
    this.pidcVarCocWpMap = definedPidcVarCocWpMap;
  }


  /**
   * @return the pidcSubVarCocWpMap
   */
  public Map<Long, Map<Long, PidcSubVarCocWp>> getPidcSubVarCocWpMap() {
    return this.pidcSubVarCocWpMap;
  }


  /**
   * @param pidcSubVarCocWpMap the pidcSubVarCocWpMap to set
   */
  public void setPidcSubVarCocWpMap(final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpMap) {
    this.pidcSubVarCocWpMap = pidcSubVarCocWpMap;
  }

}
