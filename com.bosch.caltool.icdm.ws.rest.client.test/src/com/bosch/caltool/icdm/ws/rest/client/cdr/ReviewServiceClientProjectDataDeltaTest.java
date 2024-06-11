/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.DELTA_REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class ReviewServiceClientProjectDataDeltaTest extends AbstractReviewServiceClientTest {

  /**
   * PIDC Version: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788 (V1)
   */
  private static final long PIDC_VER_ID = 1165057178L;
  /**
   * PIDC Variant: AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788 (V1)->AT
   */
  private static final long VAR_ID = 1293613521L;
  /**
   * PIDC A2L mapping ID for A2L File: <br>
   * AUDI->Diesel Engine->PC - Passenger Car->MD1-C->X_Test_HENZE_1788
   * (V1)->MMD114A0CC1788->MMD114A0CC1788_MD00_withGroups.A2L
   */
  private static final long PIDC_A2L_ID = 1328585266L;

  /**
   * project data delta Normal review + common rules + review file - paco
   *
   * @throws ApicWebServiceException error during service call
   */
  @Test
  public void testProjectDataDelta001() throws ApicWebServiceException {

    ReviewInput data = createInput("testdata/cdr/compli.cdfx", CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType(), "",
        PIDC_A2L_ID, VAR_ID);
    data.setFilesToBeReviewed(true);
    data.getPidcData().setSourcePIDCVariantId(VAR_ID);
    data.getPidcData().setSourcePidcVerId(PIDC_VER_ID);
    data.getResultData().setStartReviewType(true);
    data.getResultData().setOffReviewType(true);
    CDRReviewResult cdrReviewResult = new CDRReviewResult();
    data.setCdrReviewResult(cdrReviewResult);
    data.getCdrReviewResult().setSdomPverVarName("MMD114A0CC1788");
    data.setDeltaReview(true);
    data.setDeltaReviewType(DELTA_REVIEW_TYPE.PROJECT_DELTA_REVIEW.getDbType());
    assertNotNull(executeReview(data));
  }
}
