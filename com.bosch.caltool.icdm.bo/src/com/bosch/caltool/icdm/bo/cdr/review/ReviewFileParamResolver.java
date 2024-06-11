/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.RuleSetParameterLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;

/**
 * @author bru2cob
 */
public class ReviewFileParamResolver implements IReviewParamResolver {

  Long primaryRuleSetId;
  A2LFileInfo a2lFileContents;
  boolean hexFileAvailable;
  ServiceData serviceData;
  Map<String, Map<String, CalData>> fileCalDataMap;

  /**
   * @param hexFileAvailable
   * @param primaryRuleSetId
   * @param a2lFileContents
   * @param serviceData
   * @param fileCalDataMap
   */
  public ReviewFileParamResolver(final boolean hexFileAvailable, final Long primaryRuleSetId,
      final A2LFileInfo a2lFileContents, final ServiceData serviceData,
      final Map<String, Map<String, CalData>> fileCalDataMap) {
    this.primaryRuleSetId = primaryRuleSetId;
    this.a2lFileContents = a2lFileContents;
    this.hexFileAvailable = hexFileAvailable;
    this.serviceData = serviceData;
    this.fileCalDataMap = fileCalDataMap;
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException
   */
  @Override
  public List<String> getParameters() throws IcdmException {
    List<String> paramList = new ArrayList<String>();
    if (this.hexFileAvailable) {
      setParamList(paramList);
    }
    else {
      for (Entry<String, Map<String, CalData>> mapEntry : this.fileCalDataMap.entrySet()) {
        Map<String, CalData> calDataMap = mapEntry.getValue();
        for (String calDataName : calDataMap.keySet()) {
          paramList.add(calDataName);
        }
      }
    }
    return paramList;
  }

  /**
   * @param paramList
   * @throws DataException
   */
  private void setParamList(List<String> paramList) throws DataException {
    if (this.primaryRuleSetId == null) {
      SortedSet<Characteristic> characteristics = this.a2lFileContents.getAllSortedLabels(true);
      for (Characteristic characteristic : characteristics) {
        paramList.add(characteristic.getName());
      }
    }
    else {
      RuleSetParameterLoader paramLoader = new RuleSetParameterLoader(this.serviceData);
      Map<String, RuleSetParameter> paramsInRuleSetMap = paramLoader.getAllRuleSetParams(this.primaryRuleSetId);
      Map<String, Characteristic> allModulesLabels = this.a2lFileContents.getAllModulesLabels();
      for (RuleSetParameter ruleSetParam : paramsInRuleSetMap.values()) {
        if (allModulesLabels.containsKey(ruleSetParam.getName())) {
          paramList.add(ruleSetParam.getName());
        }
      }
    }
  }

}
