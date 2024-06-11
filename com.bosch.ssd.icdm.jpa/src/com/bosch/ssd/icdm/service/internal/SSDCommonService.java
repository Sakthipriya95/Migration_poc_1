/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.ssd.icdm.entity.VLdb2Ssd2;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.OEMRuleDescriptionInput;
import com.bosch.ssd.icdm.model.RuleIdDescriptionModel;
import com.bosch.ssd.icdm.model.SSDConfigEnums.SSDConfigParams;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDServiceMethod;
import com.bosch.ssd.icdm.service.utility.DBQueryUtils;
import com.bosch.ssd.icdm.service.utility.ServiceLogAndTransactionUtil;

/**
 * SSD Common Service
 *
 * @author SSN9COB
 */
public class SSDCommonService implements SSDServiceMethod {


  private final DBQueryUtils dbQueryUtils;

  /**
   * Cinstructor
   *
   * @param ssdNodeInfo login Service
   */
  public SSDCommonService(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.dbQueryUtils = ssdNodeInfo.getDbQueryUtils();
  }

  /**
   * Get OEM Rule information
   *
   * @param ruleIdWithRevList rule
   * @return map
   * @throws SSDiCDMInterfaceException exception
   */
  public Map<String, RuleIdDescriptionModel> getOEMDetailsForRuleId(
      final Set<OEMRuleDescriptionInput> ruleIdWithRevList)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDCommonService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    Map<String, RuleIdDescriptionModel> resultMap = new HashMap<>();
    try {
      List<Object> resultList = this.dbQueryUtils.getOEMRuleDescription(ruleIdWithRevList);
      // iterate OEM info
      resultList.forEach(obj -> {

        VLdb2Ssd2 ssd2 = (VLdb2Ssd2) obj;
        StringBuilder revFromDb = new StringBuilder(ssd2.getRevId().toString());
        String sourceRev = null;

        while (revFromDb.length() < 4) {
          revFromDb = revFromDb.insert(0, '0');
        }
        sourceRev = revFromDb.toString();
        String ruleRev = ssd2.getLabObjId() + ":" + sourceRev;
        RuleIdDescriptionModel model = new RuleIdDescriptionModel();
        model.setDataDescription(ssd2.getDataDescr());
        model.setHistoryDescription(ssd2.getHistorieDescr());
        model.setInternalAdaptionDescription(ssd2.getDescription());
        model.setRevId(ssd2.getRevId().toString());
        model.setRuleId(ssd2.getLabObjId().toString());
        model.setRuleIdWihtRev(ruleRev);
        resultMap.put(ruleRev, model);

      });
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(),false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDCommonService.class.getSimpleName(),
        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
    return resultMap;
  }

  /**
   * Get CONFIG Value
   *
   * @param name name
   * @return String
   * @throws SSDiCDMInterfaceException Exception
   */
  public String getConfigValue(final SSDConfigParams name) throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDCommonService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      String configValue = this.dbQueryUtils.getConfigValue(name);
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDCommonService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
      return configValue;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(),false);
    }
  }

  /**
   * Get PRO REV ID
   *
   * @param nodeID node
   * @return bigdecimal
   * @throws SSDiCDMInterfaceException Exception
   */
  public BigDecimal getProRevId(final BigDecimal nodeID) throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDCommonService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      BigDecimal proRevId = this.dbQueryUtils.getProRevId(nodeID);
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDCommonService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
      return proRevId;
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(),false);
    }
  }
}
