/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.caldataimport.AbstractImportRuleManager;
import com.bosch.caltool.icdm.bo.caldataimport.CalDataImporter;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDMessage;


/**
 * Import rule manager for component packages
 *
 * @author jvi6cob
 */
@Deprecated
public class CPImportRuleManager extends AbstractImportRuleManager {

  /**
   * @param serviceData
   * @param ssdServiceHandler
   */
  protected CPImportRuleManager(ServiceData serviceData, SSDServiceHandler ssdServiceHandler) {
    super(serviceData, ssdServiceHandler);
    // TODO Auto-generated constructor stub
  }

 

  /** 
   * {@inheritDoc}
   */
  @Override
  public void readRules(SSDServiceHandler ssdServiceHandler, CalDataImportData importData,
      FeatureAttributeAdapterNew adapter, ParamCollection calDataImporterObject) throws IcdmException {
    // TODO Auto-generated method stub
    
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> createRules(ParamCollection calDataImporterObject, CalDataImportData importData,
      List<CDRRule> listOfRulesCreated) throws IcdmException {
    // TODO Auto-generated method stub
    return null;
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  protected void createRuleObjects(CalDataImportData importData, ParamCollection calDataImporterObject,
      List<CDRRule> newRuleList) throws DataException {
    // TODO Auto-generated method stub
    
  }


  /** 
   * {@inheritDoc}
   */
  @Override
  public List<AbstractCommand> updateParamProps(Map<String, Map<String, String>> mapWithNewParamProps,
      Map<String, String> paramNameType, Map<String, String> classMap,
      Map<String, List<CalDataImportComparisonModel>> caldataCompMap) throws IcdmException {
    // TODO Auto-generated method stub
    return null;
  }



  /** 
   * {@inheritDoc}
   */
  @Override
  public Map<CDRRule, SSDMessage> updateRules(ParamCollection calDataImporterObject,
      List<CalDataImportComparisonModel> modifyingData, CalDataImporter calDataImporter) throws IcdmException {
    // TODO Auto-generated method stub
    return null;
  }



}
