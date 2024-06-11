/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVarRespWpLink;

/**
 * @author msp5cob
 */
public class WpLinkingFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof QnaireRespVarRespWpLink) {
      final QnaireRespVarRespWpLink qnaireRespWpLink = (QnaireRespVarRespWpLink) element;
      if (matchText(qnaireRespWpLink.getPidcVariant().getName())) {
        return true;
      }
      if (matchText(qnaireRespWpLink.getA2lWorkPackage().getName())) {
        return true;
      }
      if (matchText(qnaireRespWpLink.getA2lResponsibility().getName())) {
        return true;
      }
      if (matchText(qnaireRespWpLink.getDetails())) {
        return true;
      }
    }
    return false;
  }

}
