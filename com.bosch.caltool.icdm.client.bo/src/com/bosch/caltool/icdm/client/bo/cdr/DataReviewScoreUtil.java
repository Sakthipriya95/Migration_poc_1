/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Util class for client layer classes to access methods related to data review score
 *
 * @author pdh2cob
 */
public class DataReviewScoreUtil {

  private static final DataReviewScoreUtil dataReviewScoreUtil = new DataReviewScoreUtil();

  /**
   * @return instance of DataReviewScoreUtil
   */
  public static DataReviewScoreUtil getInstance() {
    return dataReviewScoreUtil;

  }

  private DataReviewScoreUtil() {

  }

  /**
   * @param dataReviewScore - data review score
   * @return the score display text in the format.For example '8 [75%] Review completed, ready for production'
   */
  public String getScoreDisplayExt(final DATA_REVIEW_SCORE dataReviewScore) {
    return dataReviewScore.getScoreDisplay() + " " + getDescription(dataReviewScore);
  }

  /**
   * @param dataReviewScore - data review score
   * @return the score description in the client language
   */
  public String getDescription(final DATA_REVIEW_SCORE dataReviewScore) {
    CommonDataBO data = new CommonDataBO();
    try {
      return data.getMessage(DATA_REVIEW_SCORE.getMessageGroupName(), dataReviewScore.getDescKey(), null);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.icdm.client.bo.Activator.PLUGIN_ID);
      return "";
    }
  }

}
