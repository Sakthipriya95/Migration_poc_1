/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;

/**
 * @author mkl2cob
 */
public class RvwQnaireRespFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    // Check for instance
    if (element instanceof QnaireRespStatusData) {
      // get qnaire response data
      QnaireRespStatusData qnaireData = (QnaireRespStatusData) element;
      // check the name
      if (CommonUtils.isNotNull(qnaireData.getQnaireRespName()) && matchText(qnaireData.getQnaireRespName())) {
        return true;
      }
      // check workpackage name and responsibility name
      if (matchText(qnaireData.getRespName()) || matchText(qnaireData.getWpName())) {
        return true;
      }
      if (matchText(qnaireData.getPrimaryVarName()) || matchText(qnaireData.getStatus())) {
        return true;
      }
    }
    return false;
  }

}
