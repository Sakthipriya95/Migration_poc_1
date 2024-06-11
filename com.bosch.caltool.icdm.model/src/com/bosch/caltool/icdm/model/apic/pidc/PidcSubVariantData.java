/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcSubVarCocWp;
import com.bosch.caltool.icdm.model.apic.cocwp.PidcVariantCocWp;

/**
 * @author mkl2cob
 */
public class PidcSubVariantData {

  /**
   * pidc variant id
   */
  private Long pidcVariantId;

  /**
   * pidc variant id
   */
  private Long pidcVersionId;

  private PidcVersion pidcVersBeforeUpdate;

  private PidcVersion pidcVersAfterUpdate;

  private PidcVariant pidcVarBeforeUpdate;

  private PidcVariant pidcVarAfterUpdate;
  /**
   * sub variant name attribute value
   */
  private AttributeValue subvarNameAttrValue;

  private PidcSubVariant srcPidcSubVar;

  private PidcSubVariant destPidcSubVar;

  private boolean isNameUpdated;

  private Long parentVariantId;

  private boolean newRevision;

  private boolean undoDeleteNUpdateSubVar;

  private boolean isSubVarCopiedAlongWithVariant;

  private boolean flagToUpdateDestSubVar;

  private boolean toUpdateSrcSubVarNotInDestVar;

  private boolean overrideAll;

  private Set<PidcVariantCocWp> pidcVarCocWpSet = new HashSet<>();

  private Set<PidcSubVarCocWp> createdPidcSubVarCocWpSet = new HashSet<>();

  /**
   * @return the isNewRevison
   */
  public boolean isNewRevison() {
    return this.newRevision;
  }


  /**
   * @param isNewRevison the isNewRevison to set
   */
  public void setNewRevison(final boolean isNewRevison) {
    this.newRevision = isNewRevison;
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
   * @return the pidcVersBeforeUpdate
   */
  public PidcVersion getPidcVersBeforeUpdate() {
    return this.pidcVersBeforeUpdate;
  }


  /**
   * @param pidcVersBeforeUpdate the pidcVersBeforeUpdate to set
   */
  public void setPidcVersBeforeUpdate(final PidcVersion pidcVersBeforeUpdate) {
    this.pidcVersBeforeUpdate = pidcVersBeforeUpdate;
  }


  /**
   * @return the pidcVersAfterUpdate
   */
  public PidcVersion getPidcVersAfterUpdate() {
    return this.pidcVersAfterUpdate;
  }


  /**
   * @param pidcVersAfterUpdate the pidcVersAfterUpdate to set
   */
  public void setPidcVersAfterUpdate(final PidcVersion pidcVersAfterUpdate) {
    this.pidcVersAfterUpdate = pidcVersAfterUpdate;
  }


  /**
   * @return the pidcVarBeforeUpdate
   */
  public PidcVariant getPidcVarBeforeUpdate() {
    return this.pidcVarBeforeUpdate;
  }


  /**
   * @param pidcVarBeforeUpdate the pidcVarBeforeUpdate to set
   */
  public void setPidcVarBeforeUpdate(final PidcVariant pidcVarBeforeUpdate) {
    this.pidcVarBeforeUpdate = pidcVarBeforeUpdate;
  }


  /**
   * @return the pidcVarAfterUpdate
   */
  public PidcVariant getPidcVarAfterUpdate() {
    return this.pidcVarAfterUpdate;
  }


  /**
   * @param pidcVarAfterUpdate the pidcVarAfterUpdate to set
   */
  public void setPidcVarAfterUpdate(final PidcVariant pidcVarAfterUpdate) {
    this.pidcVarAfterUpdate = pidcVarAfterUpdate;
  }


  /**
   * key - attr id ,value - attr value id
   */
  private Map<Long, Long> structAttrValueMap;


  /**
   * @return the pidcVariantId
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }


  /**
   * @param pidcVariantId the pidcVariantId to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }


  /**
   * @return the subvarNameAttrValue
   */
  public AttributeValue getSubvarNameAttrValue() {
    return this.subvarNameAttrValue;
  }


  /**
   * @param subvarNameAttrValue the subvarNameAttrValue to set
   */
  public void setSubvarNameAttrValue(final AttributeValue subvarNameAttrValue) {
    this.subvarNameAttrValue = subvarNameAttrValue;
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
   * @return the srcPidcSubVar
   */
  public PidcSubVariant getSrcPidcSubVar() {
    return this.srcPidcSubVar;
  }


  /**
   * @param srcPidcSubVar the srcPidcSubVar to set
   */
  public void setSrcPidcSubVar(final PidcSubVariant srcPidcSubVar) {
    this.srcPidcSubVar = srcPidcSubVar;
  }


  /**
   * @return the destPidcSubVar
   */
  public PidcSubVariant getDestPidcSubVar() {
    return this.destPidcSubVar;
  }


  /**
   * @param destPidcSubVar the destPidcSubVar to set
   */
  public void setDestPidcSubVar(final PidcSubVariant destPidcSubVar) {
    this.destPidcSubVar = destPidcSubVar;
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
   * @return the undoDeleteNUpdateVar
   */
  public boolean isUndoDeleteNUpdateSubVar() {
    return this.undoDeleteNUpdateSubVar;
  }


  /**
   * @param undoDeleteNUpdateSubVar the undoDeleteNUpdateVar to set
   */
  public void setUndoDeleteNUpdateSubVar(final boolean undoDeleteNUpdateSubVar) {
    this.undoDeleteNUpdateSubVar = undoDeleteNUpdateSubVar;
  }


  /**
   * @return the isSubVarCopiedAlongWithVariant
   */
  public boolean isSubVarCopiedAlongWithVariant() {
    return this.isSubVarCopiedAlongWithVariant;
  }


  /**
   * @param isSubVarCopiedAlongWithVariant the isSubVarCopiedAlongWithVariant to set
   */
  public void setSubVarCopiedAlongWithVariant(final boolean isSubVarCopiedAlongWithVariant) {
    this.isSubVarCopiedAlongWithVariant = isSubVarCopiedAlongWithVariant;
  }


  /**
   * @return the flagToUpdateDestSubVar
   */
  public boolean isFlagToUpdateDestSubVar() {
    return this.flagToUpdateDestSubVar;
  }


  /**
   * @param flagToUpdateDestSubVar the flagToUpdateDestSubVar to set
   */
  public void setFlagToUpdateDestSubVar(final boolean flagToUpdateDestSubVar) {
    this.flagToUpdateDestSubVar = flagToUpdateDestSubVar;
  }


  /**
   * @return the dummySubVarId
   */
  public Long getDummySubVarId() {
    return this.parentVariantId;
  }


  /**
   * @param dummySubVarId the dummySubVarId to set
   */
  public void setDummySubVarId(final Long dummySubVarId) {
    this.parentVariantId = dummySubVarId;
  }


  /**
   * @return the toUpdateSrcSubVarNotInDestVar
   */
  public boolean isToUpdateSrcSubVarNotInDestVar() {
    return this.toUpdateSrcSubVarNotInDestVar;
  }


  /**
   * @param toUpdateSrcSubVarNotInDestVar the toUpdateSrcSubVarNotInDestVar to set
   */
  public void setToUpdateSrcSubVarNotInDestVar(final boolean toUpdateSrcSubVarNotInDestVar) {
    this.toUpdateSrcSubVarNotInDestVar = toUpdateSrcSubVarNotInDestVar;
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
   * @return the createdPidcSubVarCocWp
   */
  public Set<PidcSubVarCocWp> getCreatedPidcSubVarCocWpSet() {
    return this.createdPidcSubVarCocWpSet;
  }


  /**
   * @param pidcSubVarCocWp the pidcSubVarCocWp to set
   */
  public void setCreatedPidcSubVarCocWpSet(final Set<PidcSubVarCocWp> pidcSubVarCocWp) {
    this.createdPidcSubVarCocWpSet = pidcSubVarCocWp;
  }


  /**
   * @return the createdPidcVarCocWpSet
   */
  public Set<PidcVariantCocWp> getPidcVarCocWpSet() {
    return this.pidcVarCocWpSet;
  }


  /**
   * @param createdPidcVarCocWpSet the createdPidcVarCocWpSet to set
   */
  public void setPidcVarCocWpSet(final Set<PidcVariantCocWp> createdPidcVarCocWpSet) {
    this.pidcVarCocWpSet = createdPidcVarCocWpSet;
  }


}
