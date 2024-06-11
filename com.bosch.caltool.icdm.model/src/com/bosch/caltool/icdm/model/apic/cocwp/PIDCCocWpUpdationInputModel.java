/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author UKT1COB
 */
public class PIDCCocWpUpdationInputModel {

  /**
   * Map of new COC WP to be created.Key - WP Div Id, Value - PidcVersCocWP
   */
  private Map<Long, PidcVersCocWp> pidcVersCocWpCreationMap = new HashMap<>();
  /**
   * Map of COC WP to be updated. Key - WP Div Id, Value - PidcVersCocWP
   */
  private Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate = new HashMap<>();
  /**
   * Map of Variant with PIDC variant COC WP to be created - key - variant id, Value - Map of WPdivId with PidcVar Coc
   * WP
   */
  private Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpCreationMap = new HashMap<>();
  /**
   * Map of Variant with PIDC variant COC WP to be updated - key - variant id, Value - Map of WPdivId with PidcVar Coc
   * WP
   */
  private Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate = new HashMap<>();
  /**
   * Map of Variant with PIDC variant COC WP to be deleted - key - variant id, Value - Map of WPdivId with PidcVar Coc
   * WP
   */
  private Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap = new HashMap<>();
  /**
   * Map of Sub Variant with PIDC sub-variant COC WP to be created - Key - subvariant id, Value - Map of WPdivId with
   * PidcSubVar Coc WP
   */
  private Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpCreationMap = new HashMap<>();
  /**
   * Map of Sub Variant with PIDC sub-variant COC WP to be updated - Key - subvariant id, Value - Map of WPdivId with
   * PidcSubVar Coc WP
   */
  private Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpBeforeUpdateMap = new HashMap<>();
  /**
   * Map of Sub Variant with PIDC sub-variant COC WP to be deleted - Key - subvariant id, Value - Map of WPdivId with
   * PidcSubVar Coc WP
   */
  private Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap = new HashMap<>();

  /**
   * Flag to identify the service is invoke on changes in used Flag
   */
  private boolean isInvokedOnUsedFlagUpd = false;

  private PidcVersion pidcVersionOld;


  /**
   * @return the pidcVersionOld
   */
  public PidcVersion getPidcVersionOld() {
    return this.pidcVersionOld;
  }


  /**
   * @param pidcVersionOld the pidcVersionOld to set
   */
  public void setPidcVersionOld(final PidcVersion pidcVersionOld) {
    this.pidcVersionOld = pidcVersionOld;
  }

  /**
   * @return the pidcVersCocWpCreationMap
   */
  public Map<Long, PidcVersCocWp> getPidcVersCocWpCreationMap() {
    return this.pidcVersCocWpCreationMap;
  }

  /**
   * @param pidcVersCocWpCreationMap the pidcVersCocWpCreationMap to set
   */
  public void setPidcVersCocWpCreationMap(final Map<Long, PidcVersCocWp> pidcVersCocWpCreationMap) {
    this.pidcVersCocWpCreationMap = pidcVersCocWpCreationMap;
  }

  /**
   * @return the pidcVarCocWpCreationMap
   */
  public Map<Long, Map<Long, PidcVariantCocWp>> getPidcVarCocWpCreationMap() {
    return this.pidcVarCocWpCreationMap;
  }

  /**
   * @param pidcVarCocWpCreationMap the pidcVarCocWpCreationMap to set
   */
  public void setPidcVarCocWpCreationMap(final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpCreationMap) {
    this.pidcVarCocWpCreationMap = pidcVarCocWpCreationMap;
  }

  /**
   * @return the pidcVersCocWpBeforeUpdate
   */
  public Map<Long, PidcVersCocWp> getPidcVersCocWpBeforeUpdate() {
    return this.pidcVersCocWpBeforeUpdate;
  }

  /**
   * @param pidcVersCocWpBeforeUpdate the pidcVersCocWpBeforeUpdate to set
   */
  public void setPidcVersCocWpBeforeUpdate(final Map<Long, PidcVersCocWp> pidcVersCocWpBeforeUpdate) {
    this.pidcVersCocWpBeforeUpdate = pidcVersCocWpBeforeUpdate;
  }

  /**
   * @return the pidcVarCocWpMapBeforeUpdate
   */
  public Map<Long, Map<Long, PidcVariantCocWp>> getPidcVarCocWpMapBeforeUpdate() {
    return this.pidcVarCocWpMapBeforeUpdate;
  }

  /**
   * @param pidcVarCocWpMapBeforeUpdate the pidcVarCocWpMapBeforeUpdate to set
   */
  public void setPidcVarCocWpMapBeforeUpdate(final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapBeforeUpdate) {
    this.pidcVarCocWpMapBeforeUpdate = pidcVarCocWpMapBeforeUpdate;
  }


  /**
   * @param pidcVarCocWpDeletionMap the pidcVarCocWpDeletionMap to set
   */
  public void setPidcVarCocWpDeletionMap(final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpDeletionMap) {
    this.pidcVarCocWpDeletionMap = pidcVarCocWpDeletionMap;
  }


  /**
   * @return the pidcVarCocWpDeletionMap
   */
  public Map<Long, Map<Long, PidcVariantCocWp>> getPidcVarCocWpDeletionMap() {
    return this.pidcVarCocWpDeletionMap;
  }

  /**
   * @return the pidcSubVarCocWpCreationMap
   */
  public Map<Long, Map<Long, PidcSubVarCocWp>> getPidcSubVarCocWpCreationMap() {
    return this.pidcSubVarCocWpCreationMap;
  }

  /**
   * @param pidcSubVarCocWpCreationMap the pidcSubVarCocWpCreationMap to set
   */
  public void setPidcSubVarCocWpCreationMap(final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpCreationMap) {
    this.pidcSubVarCocWpCreationMap = pidcSubVarCocWpCreationMap;
  }


  /**
   * @return the pidcSubVarCocWpBeforeUpdateMap
   */
  public Map<Long, Map<Long, PidcSubVarCocWp>> getPidcSubVarCocWpBeforeUpdateMap() {
    return this.pidcSubVarCocWpBeforeUpdateMap;
  }

  /**
   * @param pidcSubVarCocWpBeforeUpdateMap the pidcSubVarCocWpBeforeUpdateMap to set
   */
  public void setPidcSubVarCocWpBeforeUpdateMap(
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpBeforeUpdateMap) {
    this.pidcSubVarCocWpBeforeUpdateMap = pidcSubVarCocWpBeforeUpdateMap;
  }

  /**
   * @param pidcSubVarCocWpDeletionMap the pidcSubVarCocWpDeletionMap to set
   */
  public void setPidcSubVarCocWpDeletionMap(final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpDeletionMap) {
    this.pidcSubVarCocWpDeletionMap = pidcSubVarCocWpDeletionMap;
  }

  /**
   * @return the pidcSubVarCocWpDeletionMap
   */
  public Map<Long, Map<Long, PidcSubVarCocWp>> getPidcSubVarCocWpDeletionMap() {
    return this.pidcSubVarCocWpDeletionMap;
  }

  /**
   * @return the isInvokedOnUsedFlagUpd
   */
  public boolean isInvokedOnUsedFlagUpd() {
    return this.isInvokedOnUsedFlagUpd;
  }


  /**
   * @param isInvokedOnUsedFlagUpd the isInvokedOnUsedFlagUpd to set
   */
  public void setInvokedOnUsedFlagUpd(final boolean isInvokedOnUsedFlagUpd) {
    this.isInvokedOnUsedFlagUpd = isInvokedOnUsedFlagUpd;
  }


}
