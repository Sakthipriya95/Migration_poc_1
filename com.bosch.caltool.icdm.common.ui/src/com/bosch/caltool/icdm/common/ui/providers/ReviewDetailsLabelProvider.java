/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.bosch.caltool.icdm.client.bo.cdr.ReviewDetail;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Icdm-1290- New Label Provider for getting Values for PIDC tree
 *
 * @author rgo7cob
 */
public class ReviewDetailsLabelProvider extends StyledCellLabelProvider implements ITableLabelProvider {


  private int columnIdx;


  // NOPMD by dmo5cob on

  // 4/2/14 2:31 PM


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(final Object element) {
    if (element instanceof ReviewDetail) {
      // get the tool tip text
      return getReviewDetailTxt(element, this.columnIdx);
    }
    return "";
  }


  @Override
  public boolean isLabelProperty(final Object element, final String property) {
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnText(final Object element, final int columnIndex) { // NOPMD by dmo5cob on 4/2/14 2:31 PM
    String result = "";
    if (element instanceof ReviewDetail) {
      // get the table column text
      result = getReviewDetailTxt(element, columnIndex);
    }
    return result;
  }


  /**
   * @param element
   * @param columnIndex
   * @return
   */
  private String getReviewDetailTxt(final Object element, final int columnIndex) {
    String result;
    final ReviewDetail reviewDetail = (ReviewDetail) element;
    this.columnIdx = columnIndex;

    switch (columnIndex) {
      case CommonUIConstants.COLUMN_INDEX_0:
        // PIDC name
        result = casePidcName(reviewDetail);
        break;
      case CommonUIConstants.COLUMN_INDEX_1:
        // Variant name
        result = caseVarName(reviewDetail);
        break;
      case CommonUIConstants.COLUMN_INDEX_2:
        // Review Date
        result = caseRvwDate(reviewDetail);

        break;
      case CommonUIConstants.COLUMN_INDEX_3:
        // Review Result
        result = caseChildRvw(reviewDetail);
        break;
      case CommonUIConstants.COLUMN_INDEX_4:
        // Review Comment
        result = caseRvwComment(reviewDetail);
        break;
      case CommonUIConstants.COLUMN_INDEX_5:
        // Review Param name
        result = caseRvwParam(reviewDetail);
        break;
      default:
        result = ApicConstants.EMPTY_STRING;
        break;
    }
    return result;

  }


  /**
   * @param reviewDetail
   * @return
   */
  private String caseRvwParam(final ReviewDetail reviewDetail) {
    String result;
    if (reviewDetail.hasChildRevDetails()) {
      result = ApicConstants.EMPTY_STRING;
    }
    else {
      result = CommonUtils.checkNull(reviewDetail.getParamName());
    }
    return result;
  }


  /**
   * @param reviewDetail
   * @return
   */
  private String caseRvwComment(final ReviewDetail reviewDetail) {
    String result;
    if (reviewDetail.hasChildRevDetails()) {
      result = ApicConstants.EMPTY_STRING;
    }
    else {
      result = CommonUtils.checkNull(reviewDetail.getReviewComment());
    }
    return result;
  }


  /**
   * @param reviewDetail
   * @return
   */
  private String caseChildRvw(final ReviewDetail reviewDetail) {
    String result;
    if (reviewDetail.hasChildRevDetails()) {
      result = ApicConstants.EMPTY_STRING;
    }
    else {
      result = CommonUtils.checkNull(reviewDetail.getReviewResult());
    }
    return result;
  }


  /**
   * @param reviewDetail
   * @return
   */
  private String caseRvwDate(final ReviewDetail reviewDetail) {
    String result;
    if (isMainRow(reviewDetail)) {
      result = CommonUtils.checkNull(reviewDetail.getDateOfReviewString("yyyy-MM-dd HH:mm"));
    }
    else {
      result = ApicConstants.EMPTY_STRING;
    }
    return result;
  }


  /**
   * @param reviewDetail
   * @return
   */
  private String caseVarName(final ReviewDetail reviewDetail) {
    String result;
    if (isMainRow(reviewDetail)) {
      result = CommonUtils.checkNull(reviewDetail.getVariantName());
    }
    else {
      result = ApicConstants.EMPTY_STRING;
    }
    return result;
  }


  /**
   * @param reviewDetail
   * @return
   */
  private String casePidcName(final ReviewDetail reviewDetail) {
    String result;
    if (isMainRow(reviewDetail)) {
      result = CommonUtils.checkNull(reviewDetail.getPidcName());
    }
    else {
      result = CommonUtils.checkNull(reviewDetail.getParamName());
    }
    return result;
  }


  /**
   * @param reviewDetail
   * @return
   */
  private boolean isMainRow(final ReviewDetail reviewDetail) {
    return (reviewDetail.hasChildRevDetails() && !reviewDetail.isHasParent()) ||
        !ApicUtil.isVariantCoded(reviewDetail.getParamName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(final ILabelProviderListener iListner) {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    // Not Applicable

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Image getColumnImage(final Object arg0, final int arg1) {
    // TODO Auto-generated method stub
    return null;
  }

}
