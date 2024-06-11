/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.a2l.FunctionParamProperties;


/**
 * This class is used to filter parameter function details in AddRuleSetParameterDialog
 *
 * @author mkl2cob
 */
public class ParamFuncViewerFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {


    FunctionParamProperties list1 = (FunctionParamProperties) element;

    // filter based on param name
    if (matchText(list1.getParamName())) {
      return true;
    }
    if (new CommonDataBO().getLanguage() == Language.ENGLISH) {
      // filter based on param description for english
      if (matchText(list1.getParamLongName())) {
        return true;
      }
    }
    else {
      // filter based on param description for German
      if (matchText(list1.getParamLongNameGer())) {
        return true;
      }
    }
    // Filter based on function name
    return (matchText(list1.getFunctionName()));

  }
}
