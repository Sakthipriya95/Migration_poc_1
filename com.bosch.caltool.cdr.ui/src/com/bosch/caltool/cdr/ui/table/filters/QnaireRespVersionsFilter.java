/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;

/**
 * @author say8cob
 */
public class QnaireRespVersionsFilter extends AbstractViewerFilter {


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean result = false;
    if (element instanceof RvwQnaireRespVersion) {
      // If selected element is a BC
      final RvwQnaireRespVersion rvwQnaireRespVersion = (RvwQnaireRespVersion) element;

      // Filter the table
      result = isMatchVersNumOrRevNumOrDesc(rvwQnaireRespVersion) || matchText(rvwQnaireRespVersion.getCreatedUser()) ||
          matchText(rvwQnaireRespVersion.getReviewedUser());
    }
    return result;
  }

  /**
   * @param rvwQnaireRespVersion
   * @return
   */
  private boolean isMatchVersNumOrRevNumOrDesc(final RvwQnaireRespVersion rvwQnaireRespVersion) {
    return matchText(rvwQnaireRespVersion.getVersionName()) || matchText(rvwQnaireRespVersion.getDescription()) ||
        matchText(rvwQnaireRespVersion.getRevNum().toString());
  }

}
