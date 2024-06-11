/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import com.bosch.caltool.cdr.ui.editors.PIDCRvwResultsEditorConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewListEditorDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * This class converts the input into column data
 *
 * @author mkl2cob
 */
public class PIDCRvwResInputToColumnConverter extends AbstractNatInputToColumnConverter {

  private final ReviewListEditorDataHandler dataHandler;

  /**
   * @param dataHandler
   */
  public PIDCRvwResInputToColumnConverter(final ReviewListEditorDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    String result = null;
    if (evaluateObj instanceof ReviewVariantModel) {
      ReviewResultData cdrResult = ((ReviewVariantModel) evaluateObj).getReviewResultData();
      switch (colIndex) {
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_ICON:
          // review result
          result = "";
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DATE:
          // date and time of result
          result = this.dataHandler.getFormattedDate(cdrResult.getCdrReviewResult().getCreatedDate());
          break;
        // ICDM-2177
        case PIDCRvwResultsEditorConstants.COLNDX_PVER_NAME:
          // sdom pver name
          result = cdrResult.getPverName();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_A2L_VERSION:
          // a2l version of the review
          result = cdrResult.getCdrReviewResult().getSdomPverVarName();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_PIDC_VARIANT:
          // pidc variant
          result = cdrResult.getPidcVariantName();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_DESC:
          // description of the review
          result = cdrResult.getCdrReviewResult().getDescription();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_TYPE:
          // review type
          result = CDRConstants.REVIEW_TYPE.getType(cdrResult.getCdrReviewResult().getReviewType()).toString();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_STATUS:
          // review status
          result = CDRConstants.REVIEW_STATUS.getType(cdrResult.getCdrReviewResult().getRvwStatus()).toString();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_LOCK_STATUS:
          // ICDM-2078
          // review lock status
          result = CommonUtils.getDisplayText(CDRConstants.REVIEW_LOCK_STATUS.YES == CDRConstants.REVIEW_LOCK_STATUS
              .getType(cdrResult.getCdrReviewResult().getLockStatus()));
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_REVIEW_SCOPE:
          // review scope
          result = CDRConstants.CDR_SOURCE_TYPE.getType(cdrResult.getCdrReviewResult().getSourceType()).getUIType();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_SCOPE_NAME:
          // scope name
          result = cdrResult.getScopeName();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_CAL_ENGINEER:
          result = cdrResult.getCalEngineer();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_AUDITOR:
          result = cdrResult.getAuditor();

          break;
        case PIDCRvwResultsEditorConstants.COLNDX_PARENT_REVIEW:
          // parent review name
          result = cdrResult.getParentReview();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_RULESET_NAME:
          // rule set name
          result = cdrResult.getRuleSetName();
          break;
        case PIDCRvwResultsEditorConstants.COLNDX_BASE_REVIEW:
          // base review
          result = cdrResult.getBaseReview();
          break;
        default:
          result = "";
      }
    }
    return result;

  }

}
