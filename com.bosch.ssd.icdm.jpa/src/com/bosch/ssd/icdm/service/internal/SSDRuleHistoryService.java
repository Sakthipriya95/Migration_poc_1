/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.CDRRuleExt;
import com.bosch.ssd.icdm.model.utils.DBToModelUtil;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDServiceMethod;
import com.bosch.ssd.icdm.service.utility.DBQueryUtils;
import com.bosch.ssd.icdm.service.utility.ServiceLogAndTransactionUtil;

/**
 * This class contains all the rule history methods
 *
 * @author SSN9COB
 */
public class SSDRuleHistoryService implements SSDServiceMethod {

  /**
   *
   */
  private final SSDNodeInfoAccessor ssdNodeInfo;
  private final DBQueryUtils dbQueryUtils;

  /**
   * @param ssdNodeInfo nodeInfo
   */
  public SSDRuleHistoryService(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
    this.dbQueryUtils = ssdNodeInfo.getDbQueryUtils();
  }

  /**
   * Method to get the history of the review rules from database for a single label
   *
   * @param labelName - label
   * @param labelType - type of the label
   * @return - list of history of rules
   * @throws SSDiCDMInterfaceException Exception
   */
  @Deprecated
  public List<CDRRuleExt> getRuleHistory(final String labelName, final String labelType)
      throws SSDiCDMInterfaceException {
    return getRuleHistoryForNode(labelName, labelType, this.ssdNodeInfo.getReviewRuleNodeId());
  }

  /**
   * Method to get the history of the review rules from database for a single label
   *
   * @param labelName label Name
   * @param labelType type of Label
   * @param ssdNodeId compli Node Id
   * @return list of rule history
   * @throws SSDiCDMInterfaceException exception
   */
  @Deprecated
  public List<CDRRuleExt> getRuleHistoryForNode(final String labelName, final String labelType,
      final BigDecimal ssdNodeId)
      throws SSDiCDMInterfaceException {
    // invoke the method in DBQueryUtils to retrieve the list
    List<CDRRuleExt> cdrRuleList = new ArrayList<>();
    if (Objects.nonNull(labelName)) {
      List<Object[]> dbList = this.dbQueryUtils.getRuleHistoryForLabel(labelName, ssdNodeId);
      cdrRuleList = getCDRRuleFromDBList(dbList);
    }
    return cdrRuleList;
  }


  /**
   * Method to get the history of the review rules from database
   *
   * @param rule rule
   * @param ssdNodeId node id
   * @param isCompliRule isForCompliRule
   * @return list
   * @throws SSDiCDMInterfaceException exception
   */
  public List<CDRRuleExt> getRuleHistoryForNode(final CDRRule rule, final BigDecimal ssdNodeId,
      final boolean isCompliRule)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      // invoke the method in DBQueryUtils to retrieve the list
      List<Object[]> ruleHistoryForRule;
      if (!isCompliRule) {
        ruleHistoryForRule = this.dbQueryUtils.getRuleHistoryForRule(rule, ssdNodeId);
      }
      else {
        ruleHistoryForRule = this.dbQueryUtils.getRuleHistoryForCompliRule(rule);
      }
      List<CDRRuleExt> cdrRuleList = getCDRRuleFromDBList(ruleHistoryForRule);
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseReportService.class.getSimpleName(),
          getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), true);
      return cdrRuleList;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(),false);
    }
  }

  /**
   * Common Method used to convert DB List to CDR Rule List for all get rule history methods
   */
  private List<CDRRuleExt> getCDRRuleFromDBList(final List<Object[]> ssdList) {
    List<CDRRuleExt> cdrRuleList = new ArrayList<>();
    if (Objects.nonNull(ssdList) && !ssdList.isEmpty()) {
      for (Object[] objects : ssdList) {
        cdrRuleList.add(DBToModelUtil.convertToCDRRuleExt(objects));
      }
    }
    return cdrRuleList;
  }
}
