/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * @author RDP2COB
 */
public class DataAssessmentWPNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object returnObj = null;
    // same row as last, use its object as it's way faster than a list_get(row)
    // If object is instance of data assessment work package details value
    if (evaluateObj instanceof DaWpResp) {
      final DaWpResp wpDetails = (DaWpResp) evaluateObj;
      // Get the column index
      if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_WORK_PACKAGE) {
        // workpackage name
        returnObj = wpDetails.getA2lWpName();
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_RESPONSIBLE) {
        // responsibility name
        returnObj = wpDetails.getA2lRespName();
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_RESPONSIBLE_TYPE) {
        // resp type
        returnObj = (WpRespType.getType(wpDetails.getA2lRespType()).getDispName());
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_OVERALL_WP) {
        // is ready for production
        returnObj = (wpDetails.getWpReadyForProductionFlag());
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_WP_FINISHED) {
        // work package finished
        returnObj = (wpDetails.getWpFinishedFlag());
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_QNAIRE_ANSWERED_BASELINED) {
        // is questionnaires answered
        returnObj =
            CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.getTypeByDbCode(wpDetails.getQnairesAnsweredFlag()).getUiType();
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_PARAMETER_REVIEWED) {
        // is parameter reviewed
        returnObj = wpDetails.getParameterReviewedFlag();
      }
      else if (colIndex == DataAssessmentWorkPackagesPage.COLUMN_NUM_HEX_FILE_EQUAL_TO_REVIEWS) {
        // hex file equality
        returnObj = (wpDetails.getHexRvwEqualFlag());
      }
    }
    return returnObj;
  }


}
