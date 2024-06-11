/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author bru2cob
 */
public class ImportA2lWpRespData {

  /** The wp def vers id. */
  private Long wpDefVersId;

  /** The a 2 l file id. */
  private Long a2lFileId;
  /**
   * set of var group and wp resp mapping objects
   */
  Set<VarGrp2Wp> varGrp2WpRespSet = new HashSet<>();
  /**
   * Key - param name Value- param wp resp mapping
   */
  Map<String, Par2Wp> paramWpRespMap = new HashMap<>();

  /** The pidc version id. */
  private Long pidcVersionId;

  private A2lWpImportProfileData a2lWpImportProfileData = new A2lWpImportProfileData();

  /**
   * @return the paramWpRespMap
   */
  public Map<String, Par2Wp> getParamWpRespMap() {
    return this.paramWpRespMap;
  }


  /**
   * @param paramWpRespMap the paramWpRespMap to set
   */
  public void setParamWpRespMap(final Map<String, Par2Wp> paramWpRespMap) {
    this.paramWpRespMap = paramWpRespMap;
  }


  /**
   * @return the varGrp2WpRespSet
   */
  public Set<VarGrp2Wp> getVarGrp2WpRespSet() {
    return this.varGrp2WpRespSet;
  }


  /**
   * @param varGrp2WpRespSet the varGrp2WpRespSet to set
   */
  public void setVarGrp2WpRespSet(final Set<VarGrp2Wp> varGrp2WpRespSet) {
    this.varGrp2WpRespSet = varGrp2WpRespSet;
  }


  /**
   * @return the wpDefVersId
   */
  public Long getWpDefVersId() {
    return this.wpDefVersId;
  }


  /**
   * @param wpDefVersId the wpDefVersId to set
   */
  public void setWpDefVersId(final Long wpDefVersId) {
    this.wpDefVersId = wpDefVersId;
  }


  /**
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }


  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }

  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the a2lWpImportProfileData
   */
  public A2lWpImportProfileData getA2lWpImportProfileData() {
    return this.a2lWpImportProfileData;
  }


  /**
   * @param a2lWpImportProfileData the a2lWpImportProfileData to set
   */
  public void setA2lWpImportProfileData(final A2lWpImportProfileData a2lWpImportProfileData) {
    this.a2lWpImportProfileData = a2lWpImportProfileData;
  }
}
