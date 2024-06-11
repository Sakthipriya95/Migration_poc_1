/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.CalDataImporter;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * ICDM-1540
 *
 * @author bru2cob
 */
@Deprecated
public class FunctionImportRuleManager extends AbstractImportRuleManager {


  /**
   * @param serviceData
   * @param ssdServiceHandler
   */
  @Deprecated
  protected FunctionImportRuleManager(final ServiceData serviceData, final SSDServiceHandler ssdServiceHandler) {
    super(serviceData, ssdServiceHandler);
    // TODO Auto-generated constructor stub
  }


  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public void readRules(final SSDServiceHandler ssdServiceHandler,
      final com.bosch.caltool.icdm.model.caldataimport.CalDataImportData importData,
      final FeatureAttributeAdapterNew adapter, final ParamCollection calDataImporterObject)
      throws IcdmException {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  public Map<CDRRule, SSDMessage> createRules(final ParamCollection calDataImporterObject,
      final com.bosch.caltool.icdm.model.caldataimport.CalDataImportData importData,
      final List<CDRRule> listOfRulesCreated)
      throws IcdmException {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Deprecated
  @Override
  protected void createRuleObjects(final com.bosch.caltool.icdm.model.caldataimport.CalDataImportData importData,
      final ParamCollection calDataImporterObject, final List<CDRRule> newRuleList) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  @Deprecated
  public List<AbstractCommand> updateParamProps(final Map<String, Map<String, String>> mapWithNewParamProps,
      final Map<String, String> paramNameType, final Map<String, String> classMap,
      final Map<String, List<CalDataImportComparisonModel>> caldataCompMap)
      throws IcdmException {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> updateRules(final ParamCollection calDataImporterObject,
      final List<CalDataImportComparisonModel> modifyingData, final CalDataImporter calDataImporter)
      throws IcdmException {
    // TODO Auto-generated method stub
    return null;
  }


}
