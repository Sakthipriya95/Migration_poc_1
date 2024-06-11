/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;

/**
 * @author gge6cob
 */
public class EmrFileEmsVariantMapping implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = -600784308366209961L;
  private Map<Long, Set<Long>> emrFileEmsMap = new HashMap<>();
  private Map<Long, EmrEmissionStandard> emissionStandard = new HashMap<>();

  private Map<Long, Set<EmrPidcVariant>> emrFileEmsVariantMap = new HashMap<>();
  private Map<Long, PidcVariant> pidcVariants = new HashMap<>();

  private Map<Long, EmrFile> emrFilesMap = new HashMap<>();

  /**
   * Key - EMS ID; Value - Key-EmrVariant;Value-EmrVariant value string
   */
  private Map<Long, Map<Long, String>> emrVariantInfoMap = new HashMap<>();

  /**
   * @return the emrFileEmsMap
   */
  public Map<Long, Set<Long>> getEmrFileEmsMap() {
    return this.emrFileEmsMap;
  }

  /**
   * @param emrFileEmsMap the emrFileEmsMap to set
   */
  public void setEmrFileEmsMap(final Map<Long, Set<Long>> emrFileEmsMap) {
    this.emrFileEmsMap = emrFileEmsMap;
  }

  /**
   * @return the emissionStandard
   */
  public Map<Long, EmrEmissionStandard> getEmissionStandard() {
    return this.emissionStandard;
  }

  /**
   * @param emissionStandard the emissionStandard to set
   */
  public void setEmissionStandard(final Map<Long, EmrEmissionStandard> emissionStandard) {
    this.emissionStandard = emissionStandard;
  }

  /**
   * @return the emrFileEmsVariantMap
   */
  public Map<Long, Set<EmrPidcVariant>> getEmrFileEmsVariantMap() {
    return this.emrFileEmsVariantMap;
  }

  /**
   * @param emrFileEmsVariantMap the emrFileEmsVariantMap to set
   */
  public void setEmrFileEmsVariantMap(final Map<Long, Set<EmrPidcVariant>> emrFileEmsVariantMap) {
    this.emrFileEmsVariantMap = emrFileEmsVariantMap;
  }

  /**
   * @return the pidcVariants
   */
  public Map<Long, PidcVariant> getPidcVariants() {
    return this.pidcVariants;
  }

  /**
   * @param pidcVariants the pidcVariants to set
   */
  public void setPidcVariants(final Map<Long, PidcVariant> pidcVariants) {
    this.pidcVariants = pidcVariants;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // Not Implemented
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // Not Implemented

  }

  /**
   * @param emrFileId file Id
   * @param emsObject EMS
   */
  public void addToEmrEmsMap(final Long emrFileId, final EmrEmissionStandard emsObject) {
    if (!this.emrFileEmsMap.containsKey(emrFileId)) {
      Set<Long> emsIds = new HashSet<>();
      this.emrFileEmsMap.put(emrFileId, emsIds);
    }
    this.emrFileEmsMap.get(emrFileId).add(emsObject.getId());
    this.emissionStandard.put(emsObject.getId(), emsObject);
  }

  /**
   * @param emrFileId file Id
   * @param emrVariant EMR variant
   */
  public void addToPidcVariantMap(final long emrFileId, final EmrPidcVariant emrVariant) {
    if (!this.emrFileEmsVariantMap.containsKey(emrFileId)) {
      Set<EmrPidcVariant> variants = new HashSet<>();
      this.emrFileEmsVariantMap.put(emrFileId, variants);
    }
    this.emrFileEmsVariantMap.get(emrFileId).add(emrVariant);
  }


  /**
   * @return the emrFilesMap
   */
  public Map<Long, EmrFile> getEmrFilesMap() {
    return this.emrFilesMap;
  }


  /**
   * @param emrFilesMap the emrFilesMap to set
   */
  public void setEmrFilesMap(final Map<Long, EmrFile> emrFilesMap) {
    this.emrFilesMap = emrFilesMap;
  }

  /**
   * @param emrFile EmrFile
   */
  public void addToEmrFilesMap(final EmrFile emrFile) {
    this.emrFilesMap.put(emrFile.getId(), emrFile);
  }


  /**
   * @return the emsVariantInfoMap
   */
  public Map<Long, Map<Long, String>> getEmrVariantInfoMap() {
    return this.emrVariantInfoMap;
  }


  /**
   * @param emrVariantInfoMap the emsVariantInfoMap to set
   */
  public void setEmrVariantInfoMap(final Map<Long, Map<Long, String>> emrVariantInfoMap) {
    this.emrVariantInfoMap = emrVariantInfoMap;
  }

  /**
   * For each of the Emission standard procedure in EMR file, its variant and variant information are added to this map
   *
   * @param emsId Emission standard procedure ID
   * @param emrVariant EMR Variant value
   * @param emrVariantInfo EMR Variant Info String
   */
  public void addToEmrVariantInfoMap(final long emsId, final long emrVariant, final String emrVariantInfo) {
    if (!this.emrVariantInfoMap.containsKey(emsId)) {
      Map<Long, String> variantMap = new HashMap<>();
      this.emrVariantInfoMap.put(emsId, variantMap);
    }
    this.emrVariantInfoMap.get(emsId).put(emrVariant, emrVariantInfo);
  }
}
