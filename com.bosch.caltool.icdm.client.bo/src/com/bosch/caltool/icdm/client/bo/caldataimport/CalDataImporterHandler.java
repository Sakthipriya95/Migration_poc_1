/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.caldataimport;

import java.io.IOException;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportData;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportInput;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportSummary;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CalDataImportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class CalDataImporterHandler extends AbstractClientDataHandler {


  /**
   * Parse teh input file to a import data model
   *
   * @param fileNames set of files
   * @param funcId function ID
   * @param paramColType collection type
   * @param funcVersion function version type
   * @return import data model
   * @throws ApicWebServiceException service error
   */
  public CalDataImportData parseFile(final Set<String> fileNames, final String funcId, final String paramColType,
      final String funcVersion)
      throws ApicWebServiceException {
    CalDataImportServiceClient client = new CalDataImportServiceClient();

    return client.parsefile(fileNames, funcId, paramColType, funcVersion);
  }


  /**
   * Get the feature mapped attribute values
   *
   * @param attrId attribute ID
   * @return set of values
   * @throws ApicWebServiceException service error
   */
  public SortedSet<AttributeValue> getFeatureMappedAttributeValues(final Long attrId) throws ApicWebServiceException {
    AttributeValueServiceClient client = new AttributeValueServiceClient();

    return client.getFeatureMappedAttrVal(attrId);

  }


  /**
   * Get the attribute for the attr value
   *
   * @param val attribute value
   * @return the Attribute
   * @throws ApicWebServiceException service error
   */
  public Attribute getAttribute(final AttributeValue val) throws ApicWebServiceException {
    AttributeServiceClient client = new AttributeServiceClient();

    return client.get(val.getAttributeId());

  }


  /**
   * Create comparision objects
   *
   * @param calDataImportData import data
   * @param funcId funtion id as string
   * @param paramColType parameter collection type
   * @return import summary
   * @throws ApicWebServiceException service error
   */
  public CalDataImportData getCalDataCompareList(final CalDataImportData calDataImportData, final String funcId,
      final String paramColType)
      throws ApicWebServiceException {

    CalDataImportInput importInput = new CalDataImportInput();
    importInput.setCalDataImportData(calDataImportData);
    importInput.setFuncId(funcId);
    importInput.setParamColType(paramColType);

    return new CalDataImportServiceClient().getCalDataCompareList(importInput);

  }

  /**
   * @param model comparsion import model
   * @return the oldRefValue
   * @throws DataException cal data retrieval error
   */
  public CalData getOldRefValue(final CalDataImportComparisonModel model) throws DataException {

    ReviewRule oldRule = model.getOldRule();
    return getCalData(model, oldRule);
  }


  /**
   * @param model comparsion import model
   * @return the oldRefValue
   * @throws DataException cal data retrieval error
   */
  public CalData getNewRefValue(final CalDataImportComparisonModel model) throws DataException {
    ReviewRule rule = model.getNewRule();
    return getCalData(model, rule);
  }


  /**
   * Get the caldata object for the given rule
   *
   * @param model
   * @param rule
   * @return
   * @throws DataException
   */
  private CalData getCalData(final CalDataImportComparisonModel model, final ReviewRule rule) throws DataException {
    CalData calData = rule == null ? null : getRefValForRule(rule);

    if ((calData == null) && (null != rule) && ("VALUE").equals(model.getParamType()) &&
        CommonUtils.isNotNull(rule.getRefValue())) {

      String paramName = rule.getParameterName();
      String dcmStr = CalDataUtil.createDCMStringForNumber(paramName, rule.getUnit(), rule.getRefValue().toString());
      calData = CalDataUtil.dcmToCalDataExt(dcmStr, paramName, CDMLogger.getInstance());
    }

    return calData;
  }

  /**
   * Get the ref value for the rule
   *
   * @param rule Review Rule
   * @return Ref value as cal data
   * @throws DataException cal data creation error
   */
  public CalData getRefValForRule(final ReviewRule rule) throws DataException {
    CalData caldataObj = null;
    try {
      caldataObj = CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      throw new DataException("error when creating cal data object" + exp);
    }

    return caldataObj;
  }

  /**
   * Get old ref value display string
   *
   * @param calCompModel import comparision model
   * @return the oldRefValue for display
   * @throws DataException ref value creation error
   */
  public String getOldRefValueDisplay(final CalDataImportComparisonModel calCompModel) throws DataException {
    CalData value = getOldRefValue(calCompModel);
    return value == null ? "" : value.getCalDataPhy().getSimpleDisplayValue();
  }

  /**
   * @param calDataImportData import data
   * @param funcId funtion id as string
   * @param paramColType parameter collection type
   * @return import summary
   * @throws ApicWebServiceException service error
   */
  public CalDataImportSummary saveParamAndRules(final CalDataImportData calDataImportData, final String funcId,
      final String paramColType)
      throws ApicWebServiceException {

    CalDataImportInput input = new CalDataImportInput();
    input.setCalDataImportData(calDataImportData);
    input.setFuncId(funcId);
    input.setParamColType(paramColType);

    return new CalDataImportServiceClient().createParmandRules(input);
  }
}
