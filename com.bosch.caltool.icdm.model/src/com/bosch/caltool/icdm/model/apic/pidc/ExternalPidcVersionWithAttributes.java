/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author elm1cob
 */
public class ExternalPidcVersionWithAttributes {

  private ExternalPidcVersionInfo pidcVersionInfo;

  private Map<Long, PidcVersionAttribute> pidcAttributeMap = new HashMap<>();

  private Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantMap = new HashMap<>();


  /**
   * key - sub var id, value -ProjectObjectWithAttributes
   */
  private Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantMap =
      new HashMap<>();

  /**
   * key- variant id , value - sub variant id set
   */
  private Map<Long, Set<Long>> varWithSubVarIds;

  private Map<Long, Attribute> attributeMap = new HashMap<>();

  private Map<Long, AttributeValue> attributeValueMap = new HashMap<>();


  /**
   * @return the extPidcVersionInfo
   */
  public ExternalPidcVersionInfo getPidcVersionInfo() {
    return this.pidcVersionInfo;
  }


  /**
   * @param extPidcVersionInfo the extPidcVersionInfo to set
   */
  public void setPidcVersionInfo(final ExternalPidcVersionInfo extPidcVersionInfo) {
    this.pidcVersionInfo = extPidcVersionInfo;
  }

  /**
   * @return the pidcAttributeMap
   */
  public Map<Long, PidcVersionAttribute> getPidcAttributeMap() {
    return this.pidcAttributeMap;
  }

  /**
   * @param pidcAttributeMap the pidcAttributeMap to set
   */
  public void setPidcAttributeMap(final Map<Long, PidcVersionAttribute> pidcAttributeMap) {
    this.pidcAttributeMap = pidcAttributeMap;
  }

  /**
   * @return the variantMap
   */
  public Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> getVariantMap() {
    return this.variantMap;
  }

  /**
   * @param variantMap the variantMap to set
   */
  public void setVariantMap(
      final Map<Long, ProjectObjectWithAttributes<PidcVariant, PidcVariantAttribute>> variantMap) {
    this.variantMap = variantMap;
  }

  /**
   * @return the attributeMap
   */
  public Map<Long, Attribute> getAttributeMap() {
    return this.attributeMap;
  }

  /**
   * @param attributeMap the attributeMap to set
   */
  public void setAttributeMap(final Map<Long, Attribute> attributeMap) {
    this.attributeMap = attributeMap;
  }

  /**
   * @return the attributeValueMap
   */
  public Map<Long, AttributeValue> getAttributeValueMap() {
    return this.attributeValueMap;
  }

  /**
   * @param attributeValueMap the attributeValueMap to set
   */
  public void setAttributeValueMap(final Map<Long, AttributeValue> attributeValueMap) {
    this.attributeValueMap = attributeValueMap;
  }

  /**
   * @return the subVariantMap
   */
  public Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> getSubVariantMap() {
    return this.subVariantMap;
  }


  /**
   * @param subVariantMap2 the subVariantMap to set
   */
  public void setSubVariantMap(
      final Map<Long, ProjectObjectWithAttributes<PidcSubVariant, PidcSubVariantAttribute>> subVariantMap2) {
    this.subVariantMap = subVariantMap2;
  }

  /**
   * @return the varWithSubVarIds
   */
  public Map<Long, Set<Long>> getVarWithSubVarIds() {
    return this.varWithSubVarIds;
  }

  /**
   * @param varWithSubVarIds the varWithSubVarIds to set
   */
  public void setVarWithSubVarIds(final Map<Long, Set<Long>> varWithSubVarIds) {
    this.varWithSubVarIds = varWithSubVarIds;
  }
}
