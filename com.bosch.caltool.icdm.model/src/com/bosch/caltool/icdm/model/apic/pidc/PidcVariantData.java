/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;

/**
 * @author mkl2cob
 */
public class PidcVariantData {

  private PidcVersion pidcVersion;

  private AttributeValue varNameAttrValue;

  private PidcVariant srcPidcVar;

  private PidcVariant destPidcVar;

  private boolean isNameUpdated;

  private Long dummyVariantId;

  private boolean isNewRevision;
  /**
   * key - attr id ,value - attr value id
   */
  private Map<Long, Long> structAttrValueMap;

  private Long riskDefId;

  private boolean undoDeleteNUpdateVar;

  private boolean isSubVarCopiedAlongWithVar;

  private boolean isUndoDeleteNUpdateSubVar;

  private SortedSet<PidcSubVariant> srcSubVarSet;

  private boolean overrideAll;

  private boolean overrideAllVarAttr;

  private Set<PidcVariantCocWp> createdPidcVarCocWpSet = new HashSet<>();

  /**
   * @return the pidcVersionId
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersion(final PidcVersion pidcVersionId) {
    this.pidcVersion = pidcVersionId;
  }


  /**
   * @return the varNameAttrValue
   */
  public AttributeValue getVarNameAttrValue() {
    return this.varNameAttrValue;
  }


  /**
   * @param varNameAttrValue the varNameAttrValue to set
   */
  public void setVarNameAttrValue(final AttributeValue varNameAttrValue) {
    this.varNameAttrValue = varNameAttrValue;
  }


  /**
   * @return the structAttrValueMap
   */
  public Map<Long, Long> getStructAttrValueMap() {
    return this.structAttrValueMap;
  }


  /**
   * @param structAttrValueMap the structAttrValueMap to set
   */
  public void setStructAttrValueMap(final Map<Long, Long> structAttrValueMap) {
    this.structAttrValueMap = structAttrValueMap;
  }


  /**
   * @return the pidcVar
   */
  public PidcVariant getSrcPidcVar() {
    return this.srcPidcVar;
  }


  /**
   * @param pidcVar the pidcVar to set
   */
  public void setSrcPidcVar(final PidcVariant pidcVar) {
    this.srcPidcVar = pidcVar;
  }


  /**
   * @return the destPidcVar
   */
  public PidcVariant getDestPidcVar() {
    return this.destPidcVar;
  }


  /**
   * @param destPidcVar the destPidcVar to set
   */
  public void setDestPidcVar(final PidcVariant destPidcVar) {
    this.destPidcVar = destPidcVar;
  }


  /**
   * @return the isNameUpdated
   */
  public boolean isNameUpdated() {
    return this.isNameUpdated;
  }


  /**
   * @param isNameUpdated the isNameUpdated to set
   */
  public void setNameUpdated(final boolean isNameUpdated) {
    this.isNameUpdated = isNameUpdated;
  }


  /**
   * @return the dummyVariantId
   */
  public Long getDummyVariantId() {
    return this.dummyVariantId;
  }


  /**
   * @param dummyVariantId the dummyVariantId to set
   */
  public void setDummyVariantId(final Long dummyVariantId) {
    this.dummyVariantId = dummyVariantId;
  }


  /**
   * @return the isNewRevision
   */
  public boolean isNewRevision() {
    return this.isNewRevision;
  }


  /**
   * @return the riskDefId
   */
  public Long getRiskDefId() {
    return this.riskDefId;
  }


  /**
   * @param riskDefId the riskDefId to set
   */
  public void setRiskDefId(final Long riskDefId) {
    this.riskDefId = riskDefId;
  }


  /**
   * @return the undoDeleteNUpdateSubVar
   */
  public boolean isUndoDeleteNUpdateVar() {
    return this.undoDeleteNUpdateVar;
  }


  /**
   * @param undoDeleteNUpdateVar the undoDeleteNUpdateSubVar to set
   */
  public void setUndoDeleteNUpdateVar(final boolean undoDeleteNUpdateVar) {
    this.undoDeleteNUpdateVar = undoDeleteNUpdateVar;
  }


  /**
   * @param isNewRevision the isNewRevision to set
   */
  public void setNewRevision(final Boolean isNewRevision) {
    this.isNewRevision = isNewRevision;
  }


  /**
   * @return the isSubVarCopiedAlongWithVar
   */
  public boolean isSubVarCopiedAlongWithVar() {
    return this.isSubVarCopiedAlongWithVar;
  }


  /**
   * @param isSubVarCopiedAlongWithVar the isSubVarCopiedAlongWithVar to set
   */
  public void setSubVarCopiedAlongWithVar(final boolean isSubVarCopiedAlongWithVar) {
    this.isSubVarCopiedAlongWithVar = isSubVarCopiedAlongWithVar;
  }


  /**
   * @return the isUndoDeleteNUpdateSubVar
   */
  public boolean isUndoDeleteNUpdateSubVar() {
    return this.isUndoDeleteNUpdateSubVar;
  }


  /**
   * @param isUndoDeleteNUpdateSubVar the isUndoDeleteNUpdateSubVar to set
   */
  public void setUndoDeleteNUpdateSubVar(final boolean isUndoDeleteNUpdateSubVar) {
    this.isUndoDeleteNUpdateSubVar = isUndoDeleteNUpdateSubVar;
  }


  /**
   * @return the srcSubVarSet
   */
  public SortedSet<PidcSubVariant> getSrcSubVarSet() {
    return this.srcSubVarSet;
  }


  /**
   * @param srcSubVarSet the srcSubVarSet to set
   */
  public void setSrcSubVarSet(final SortedSet<PidcSubVariant> srcSubVarSet) {
    this.srcSubVarSet = srcSubVarSet;
  }

  /**
   * @return the overrideAll
   */
  public boolean isOverrideAll() {
    return this.overrideAll;
  }


  /**
   * @param overrideAll the overrideAll to set
   */
  public void setOverrideAll(final boolean overrideAll) {
    this.overrideAll = overrideAll;
  }


  /**
   * @return the overrideAllVarAttr
   */
  public boolean isOverrideAllVarAttr() {
    return this.overrideAllVarAttr;
  }


  /**
   * @param overrideAllVarAttr the overrideAllVarAttr to set
   */
  public void setOverrideAllVarAttr(final boolean overrideAllVarAttr) {
    this.overrideAllVarAttr = overrideAllVarAttr;
  }


  /**
   * @return the createdPidcVarCocWp
   */
  public Set<PidcVariantCocWp> getCreatedPidcVarCocWpSet() {
    return this.createdPidcVarCocWpSet;
  }


  /**
   * @param createdPidcVarCocWp the createdPidcVarCocWp to set
   */
  public void setCreatedPidcVarCocWpSet(final Set<PidcVariantCocWp> createdPidcVarCocWp) {
    this.createdPidcVarCocWpSet = createdPidcVarCocWp;
  }

}
