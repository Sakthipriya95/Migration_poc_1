/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespVarRespWpLink;

/**
 * @author dmr1cob
 */
public class VariantLinkingFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof QnaireRespVarRespWpLink) {
      final QnaireRespVarRespWpLink qnaireRespVariantLink = (QnaireRespVarRespWpLink) element;
      if (matchText(qnaireRespVariantLink.getPidcVariant().getName())) {
        return true;
      }
      if (matchText(qnaireRespVariantLink.getDetails())) {
        return true;
      }
    }
    return false;
  }

}
