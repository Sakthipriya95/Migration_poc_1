/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;

/**
 * @author UKT1COB
 */
public class PIDCCocWpUpdationModel extends PIDCCocWpUpdationInputModel {


  /**
   * Map of COC WP to be updated. Key - WP Div Id, Value - PidcVersCocWP
   */
  private Map<Long, PidcVersCocWp> pidcVersCocWpAfterUpdate = new HashMap<>();
  /**
   * Map of Variant with PIDC variant COC WP to be updated - key - variant id, Value - Map of WPdivId with PidcVar Coc
   * WP
   */
  private Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapAfterUpdate = new HashMap<>();
  /**
   * Map of Sub Variant with PIDC sub-variant COC WP to be updated - Key - subvariant id, Value - Map of WPdivId with
   * PidcSubVar Coc WP
   */
  private Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpAfterUpdateMap = new HashMap<>();

  private PidcVersion pidcVersion;

  private PidcVersion oldPidcVersion;

  private Map<Long, PidcVariant> pidcVariantMap = new HashMap<>();

  private Map<Long, PidcSubVariant> pidcSubVariantMap = new HashMap<>();
  /**
   * List of newly created Usecase section
   */
  private List<UsecaseFavorite> listOfNewlyCreatedUcFav = new ArrayList<>();
  /**
   * List of Deleted Usecase section
   */
  private List<UsecaseFavorite> listOfDelUcFav = new ArrayList<>();

  /**
   * @return the pidcVersCocWpAfterUpdate
   */
  public Map<Long, PidcVersCocWp> getPidcVersCocWpAfterUpdate() {
    return this.pidcVersCocWpAfterUpdate;
  }

  /**
   * @param pidcVersCocWpAfterUpdate the pidcVersCocWpAfterUpdate to set
   */
  public void setPidcVersCocWpAfterUpdate(final Map<Long, PidcVersCocWp> pidcVersCocWpAfterUpdate) {
    this.pidcVersCocWpAfterUpdate = pidcVersCocWpAfterUpdate;
  }

  /**
   * @return the pidcVarCocWpMapAfterUpdate
   */
  public Map<Long, Map<Long, PidcVariantCocWp>> getPidcVarCocWpMapAfterUpdate() {
    return this.pidcVarCocWpMapAfterUpdate;
  }

  /**
   * @param pidcVarCocWpMapAfterUpdate the pidcVarCocWpMapAfterUpdate to set
   */
  public void setPidcVarCocWpMapAfterUpdate(final Map<Long, Map<Long, PidcVariantCocWp>> pidcVarCocWpMapAfterUpdate) {
    this.pidcVarCocWpMapAfterUpdate = pidcVarCocWpMapAfterUpdate;
  }

  /**
   * @return the pidcSubVarCocWpAfterUpdateMap
   */
  public Map<Long, Map<Long, PidcSubVarCocWp>> getPidcSubVarCocWpAfterUpdateMap() {
    return this.pidcSubVarCocWpAfterUpdateMap;
  }

  /**
   * @param pidcSubVarCocWpAfterUpdateMap the pidcSubVarCocWpAfterUpdateMap to set
   */
  public void setPidcSubVarCocWpAfterUpdateMap(
      final Map<Long, Map<Long, PidcSubVarCocWp>> pidcSubVarCocWpAfterUpdateMap) {
    this.pidcSubVarCocWpAfterUpdateMap = pidcSubVarCocWpAfterUpdateMap;
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


  /**
   * @return the pidcVariantMap
   */
  public Map<Long, PidcVariant> getPidcVariantMap() {
    return this.pidcVariantMap;
  }


  /**
   * @return the pidcSubVariantMap
   */
  public Map<Long, PidcSubVariant> getPidcSubVariantMap() {
    return this.pidcSubVariantMap;
  }


  /**
   * @param pidcVariantMap the pidcVariantMap to set
   */
  public void setPidcVariantMap(final Map<Long, PidcVariant> pidcVariantMap) {
    this.pidcVariantMap = pidcVariantMap;
  }


  /**
   * @param pidcSubVariantMap the pidcSubVariantMap to set
   */
  public void setPidcSubVariantMap(final Map<Long, PidcSubVariant> pidcSubVariantMap) {
    this.pidcSubVariantMap = pidcSubVariantMap;
  }


  /**
   * @return the listOfNewlyCreatedUcFav
   */
  public List<UsecaseFavorite> getListOfNewlyCreatedUcFav() {
    return this.listOfNewlyCreatedUcFav;
  }


  /**
   * @param listOfNewlyCreatedUcFav the listOfNewlyCreatedUcFav to set
   */
  public void setListOfNewlyCreatedUcFav(final List<UsecaseFavorite> listOfNewlyCreatedUcFav) {
    this.listOfNewlyCreatedUcFav = listOfNewlyCreatedUcFav;
  }


  /**
   * @return the listOfDelUcFav
   */
  public List<UsecaseFavorite> getListOfDelUcFav() {
    return this.listOfDelUcFav;
  }


  /**
   * @param listOfDelUcFav the listOfDelUcFav to set
   */
  public void setListOfDelUcFav(final List<UsecaseFavorite> listOfDelUcFav) {
    this.listOfDelUcFav = listOfDelUcFav;
  }

  /**
   * @return the oldPidcVersion
   */
  public PidcVersion getOldPidcVersion() {
    return this.oldPidcVersion;
  }

  /**
   * @param oldPidcVersion the oldPidcVersion to set
   */
  public void setOldPidcVersion(final PidcVersion oldPidcVersion) {
    this.oldPidcVersion = oldPidcVersion;
  }

  

}

