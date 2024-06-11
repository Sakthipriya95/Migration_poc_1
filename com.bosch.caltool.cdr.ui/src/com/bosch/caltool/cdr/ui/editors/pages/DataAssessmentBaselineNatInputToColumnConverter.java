/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.FILE_ARCHIVAL_STATUS;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;

/**
 * @author ajk2cob
 */
public class DataAssessmentBaselineNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object returnObj = null;
    // same row as last, use its object as it's way faster than a list_get(row)
    // If object is instance of data assessment baseline details value
    if (evaluateObj instanceof DaDataAssessment) {
      final DaDataAssessment baselineDetails = (DaDataAssessment) evaluateObj;
      // Get the column index
      if (colIndex == 0) {
        // baseline name
        returnObj = baselineDetails.getBaselineName();
      }
      else if (colIndex == 1) {
        // baseline remark
        returnObj = CommonUtils.isNotEmptyString(baselineDetails.getDescription()) ? baselineDetails.getDescription()
            : CommonUtilConstants.EMPTY_STRING;
      }
      else if (colIndex == 2) {
        // data assessment Pidc Varaint
        returnObj = CommonUtils.isNotEmptyString(baselineDetails.getVariantName()) ? baselineDetails.getVariantName()
            : CDRConstants.NO_VARIANT;
      }
      else {
        returnObj = getColumnValueForOtherColumns(colIndex,baselineDetails);
      }
    }
    return returnObj;
  }
  
  private Object getColumnValueForOtherColumns(final int colIndex, final DaDataAssessment baselineDetails) {
    Object returnObj = null;
    if (colIndex == 3) {
      // baseline created on
      returnObj = CommonUtils.isNotEmptyString(baselineDetails.getCreatedDate()) ? baselineDetails.getCreatedDate()
          : CommonUtilConstants.EMPTY_STRING;
    }
    else if (colIndex == 4) {
      // data assessment report type
      returnObj = CommonUtils.isNotEmptyString(baselineDetails.getTypeOfAssignment())
          ? DataAssmntReportDataHandler.getTypeOfAssessmentDisplayText(baselineDetails.getTypeOfAssignment())
          : CommonUtilConstants.EMPTY_STRING;
    }
    else if (colIndex == 5) {
      // data assessment archieved file link
      returnObj = DataAssmntReportDataHandler.getFileName(baselineDetails);
    }
    else if (colIndex == 6) {
      FILE_ARCHIVAL_STATUS archivalStatus =
          CDRConstants.FILE_ARCHIVAL_STATUS.getTypeByDbCode(baselineDetails.getFileArchivalStatus());
      // data assessment archieved file status
      returnObj = CommonUtils.isNotEmptyString(archivalStatus.getUiType()) ? archivalStatus.getUiType()
          : CommonUtilConstants.EMPTY_STRING;
    }
    else if (colIndex == 7) {
      // data assessment archieved file link
      returnObj = "Upload Data Assessment files to SharePoint";
    }
    return returnObj;
    
  }

}
