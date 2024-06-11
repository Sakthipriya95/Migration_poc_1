/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Locale;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.FunctionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewDetailsData;

/**
 * @author dmr1cob
 */
public class ReviewDetailDataFetcher {


  private final ServiceData serviceData;

  /**
   * @param serviceData serviceData
   * @param logger logger
   */
  public ReviewDetailDataFetcher(final ServiceData serviceData) {
    super();
    this.serviceData = serviceData;
  }

  /**
   * @param resultId resultId
   * @param paramName paramName
   * @return ReviewDetailsData
   * @throws IcdmException IcdmException
   */
  public ReviewDetailsData getReviewDetailsData(final Long resultId, final String paramName) throws IcdmException {
    CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(this.serviceData);
    RuleSetLoader ruleSetLoader = new RuleSetLoader(this.serviceData);
    FunctionLoader functionLoader = new FunctionLoader(this.serviceData);
    ReviewDetailsData reviewDetailsData = new ReviewDetailsData();
    CDRResultParameterLoader cdrResultParameterLoader = new CDRResultParameterLoader(this.serviceData);
    Map<Long, CDRResultParameter> cdrResultParameterMap =
        cdrResultParameterLoader.getByResultObj(cdrReviewResultLoader.getEntityObject(resultId));
    String funcName = "";
    if (null != paramName) {
      for (CDRResultParameter cdrresultParam : cdrResultParameterMap.values()) {
        if (paramName.equals(cdrresultParam.getName())) {
          reviewDetailsData.setCdrResultParameter(cdrresultParam);
          funcName = cdrresultParam.getFuncName();
        }
      }
    }

    CDRReviewResult cdrReviewResult = cdrReviewResultLoader.getDataObjectByID(resultId);
    if (null != cdrReviewResult.getRsetId()) {
      reviewDetailsData.setRuleSet(ruleSetLoader.getDataObjectByID(cdrReviewResult.getRsetId()));
    }
    else {
      reviewDetailsData.setFunction(functionLoader.getFunctionsByName(funcName.toUpperCase(Locale.getDefault())));
    }
    return reviewDetailsData;

  }
}
