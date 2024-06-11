/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.caldataimport.ICalDataImporterObject;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.ICalDataImportParamDetailsLoader;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;


/**
 * @author bne4cob
 */
@Deprecated
public final class CompPkgCaldataImporterObject implements ICalDataImporterObject {

  /**
   * Component package
   */
  private final CompPkg compPkg;

  CompPkgCaldataImporterObject(final CompPkg compPkg) {
    this.compPkg = compPkg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getID() {
    return this.compPkg.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.compPkg.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    return this.compPkg.isModifiable();
  }

  /**
   * Returns the SSD Use Case enum, or null if not set.
   *
   * @return SSD Use Case enum value
   */
  @Override
  public SSDCase getSsdUseCase() {
    return SSDCase.getType(getEntityProvider().getDbCompPkg(getID()).getSsdUsecase());
  }

  /**
   * @return
   */
  private CPEntityProvider getEntityProvider() {
    return this.compPkg.getEntityProvider();
  }

  /**
   * Returns the string representation of SSD Use Case. Should be used, only when the text is to be displayed directly.
   *
   * @return SSD Use Case String
   */
  public String getSsdUseCaseStr() {
    SSDCase scase = getSsdUseCase();
    return scase == null ? "" : scase.getCharacter();
  }

  /**
   * Returns SSD Param Class enum, or null if not set. directly.
   *
   * @return SSD Param Class enum value
   */
  @Override
  public ParameterClass getSsdParamClass() {
    return ParameterClass.getType(getEntityProvider().getDbCompPkg(getID()).getSsdParamClass());
  }

  /**
   * Returns the string representation of SSD Param Class. Should be used, only when the text is to be displayed
   * directly.
   *
   * @return SSD Param Class String
   */
  public String getSsdParamClassStr() {
    ParameterClass pClass = getSsdParamClass();
    return pClass == null ? "" : pClass.name();
  }


  /**
   * @return SSD Node ID
   */
  @Override
  public Long getSsdNodeID() {
    return getEntityProvider().getDbCompPkg(getID()).getSsdNodeId();
  }

  /**
   * iCDM-1326 <br>
   * Gets the configured SSD Vers node id for creation of label list for ssd release
   *
   * @return ssd version node id
   */
  @Override
  public Long getSsdVersNodeID() {
    return getEntityProvider().getDbCompPkg(getID()).getSsdVersNodeId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<IAttributeMappedObject> getAttrMappedObjects() {
    Collection<IAttributeMappedObject> mappedObject = new TreeSet<IAttributeMappedObject>();
    mappedObject.add(this.compPkg);
    return mappedObject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ICalDataImportParamDetailsLoader getParamDetailsLoader() {
    return new NECompPkgParamValidator(this.compPkg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean enableLabelListCreation() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getObjectTypeName() {
    return ApicConstants.COMP_PKG;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isInputParamsChecked() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractImportRuleManager getImportRuleManager() {
    return null;// new CPImportRuleManager(this.compPkg);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean addParamsFromFile() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AbstractDataCommand getCreateParamsCommands(final List<String> paramsTobeAdded) {
    // Not applicable
    return null;
  }

}
