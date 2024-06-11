/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.caldataimport;

import java.util.Collection;
import java.util.List;

import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.dmframework.bo.AbstractDataCommand;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.ICalDataImportParamDetailsLoader;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;

/**
 * @author dmo5cob This interface wraps the import objects like CompPkg,RuleSet etc
 */

public interface ICalDataImporterObject {

  /**
   * Retrieve the mapped ssd node id
   *
   * @return SsdNodeID
   */
  public Long getSsdNodeID();

  /**
   * Retrieve the mapped ssd version node id
   *
   * @return SsdVersNodeID
   */
  public Long getSsdVersNodeID();

  /**
   * Retrieve the mapped SsdUseCase
   *
   * @return SsdUseCase
   */
  public SSDCase getSsdUseCase();

  /**
   * Retrieve the mapped SsdParamClass
   *
   * @return SsdParamClass
   */
  public ParameterClass getSsdParamClass();

  /**
   * @return name
   */
  public String getName();

  /**
   * @return collection of IAttributeMappedObjects
   * @throws SsdInterfaceException
   */
  public Collection<IAttributeMappedObject> getAttrMappedObjects() throws SsdInterfaceException;

  /**
   * @return ICalDataImportParamDetailsLoader
   */
  public ICalDataImportParamDetailsLoader getParamDetailsLoader();

  /**
   * @return boolean
   */
  public boolean enableLabelListCreation();

  /**
   * @return boolean
   */
  public boolean isModifiable();

  /**
   * @return Whether it is componenet package / rule set . Can be used in the title description
   */
  public String getObjectTypeName();

  /**
   * @param paramsTobeAdded List<String>
   * @return List<AbstractDataCommand>
   */
  public AbstractDataCommand getCreateParamsCommands(List<String> paramsTobeAdded);

  /**
   * @return boolean
   */
  public boolean isInputParamsChecked();

  /**
   * @return id
   */
  public Long getID();

  /**
   * Returns the associated ImportRuleManager
   *
   * @return AbstractImportRuleManager
   */
  public AbstractImportRuleManager getImportRuleManager();

  /**
   * @return true if the additional params in the imported file has to be added to ruleSet
   */
  public boolean addParamsFromFile();
}
