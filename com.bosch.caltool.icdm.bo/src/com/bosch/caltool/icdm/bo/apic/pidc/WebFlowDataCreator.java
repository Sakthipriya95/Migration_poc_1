/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.Map;

import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author dmr1cob
 */
public class WebFlowDataCreator {

  private PidcVersionAttributeModel pidcVersAttrModel;

  private Map<Long, AliasDef> aliasDefMap;

  private PidcVariant pidcVariant;

  private AliasDef aliasDef;

  private Map<Long, PidcVariantAttribute> allPidcVarAttr;

  private Map<Long, PidcVersionAttribute> allPidcVersAttr;

  private Map<Long, IProjectAttribute> allProjAttr;

  private Map<Long, AliasDetail> aliasDetailMap;

  /**
   * @return the pidcVersAttrModel
   */
  public PidcVersionAttributeModel getPidcVersAttrModel() {
    return this.pidcVersAttrModel;
  }


  /**
   * @param pidcVersAttrModel the pidcVersAttrModel to set
   */
  public void setPidcVersAttrModel(final PidcVersionAttributeModel pidcVersAttrModel) {
    this.pidcVersAttrModel = pidcVersAttrModel;
  }


  /**
   * @return the aliasDefMap
   */
  public Map<Long, AliasDef> getAliasDefMap() {
    return this.aliasDefMap;
  }


  /**
   * @param aliasDefMap the aliasDefMap to set
   */
  public void setAliasDefMap(final Map<Long, AliasDef> aliasDefMap) {
    this.aliasDefMap = aliasDefMap;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @return the aliasDef
   */
  public AliasDef getAliasDef() {
    return this.aliasDef;
  }


  /**
   * @param aliasDef the aliasDef to set
   */
  public void setAliasDef(final AliasDef aliasDef) {
    this.aliasDef = aliasDef;
  }


  /**
   * @return the allPidcVarAttr
   */
  public Map<Long, PidcVariantAttribute> getAllPidcVarAttr() {
    return this.allPidcVarAttr;
  }


  /**
   * @return the allPidcVersAttr
   */
  public Map<Long, PidcVersionAttribute> getAllPidcVersAttr() {
    return this.allPidcVersAttr;
  }


  /**
   * @param allPidcVarAttr the allPidcVarAttr to set
   */
  public void setAllPidcVarAttr(final Map<Long, PidcVariantAttribute> allPidcVarAttr) {
    this.allPidcVarAttr = allPidcVarAttr;
  }


  /**
   * @param allPidcVersAttr the allPidcVersAttr to set
   */
  public void setAllPidcVersAttr(final Map<Long, PidcVersionAttribute> allPidcVersAttr) {
    this.allPidcVersAttr = allPidcVersAttr;
  }


  /**
   * @return the allProjAttr
   */
  public Map<Long, IProjectAttribute> getAllProjAttr() {
    return this.allProjAttr;
  }


  /**
   * @param allProjAttr the allProjAttr to set
   */
  public void setAllProjAttr(final Map<Long, IProjectAttribute> allProjAttr) {
    this.allProjAttr = allProjAttr;
  }


  /**
   * @return the aliasDetailMap
   */
  public Map<Long, AliasDetail> getAliasDetailMap() {
    return this.aliasDetailMap;
  }


  /**
   * @param aliasDetailMap the aliasDetailMap to set
   */
  public void setAliasDetailMap(final Map<Long, AliasDetail> aliasDetailMap) {
    this.aliasDetailMap = aliasDetailMap;
  }

}
