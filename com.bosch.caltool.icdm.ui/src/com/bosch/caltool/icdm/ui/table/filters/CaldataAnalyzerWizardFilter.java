/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;


/**
 * @author pdh2cob
 *
 */
public class CaldataAnalyzerWizardFilter extends AbstractViewerFilter {

  /** 
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(Object element) {
    boolean returnVal = false;
    String string = "";
    if(element instanceof ParameterFilterLabel){
    ParameterFilterLabel labelPattern = (ParameterFilterLabel) element;
    string = labelPattern.getLabel();
    }
    if(element instanceof FunctionFilter){
      FunctionFilter pattern = (FunctionFilter) element;
      string = pattern.getFunctionName();
      }
    if(element instanceof SystemConstantFilter){
      SystemConstantFilter pattern = (SystemConstantFilter) element;
      string = pattern.getSystemConstantName();
      }
    if(element instanceof CustomerFilter){
      CustomerFilter pattern = (CustomerFilter) element;
      string = pattern.getCustomerName();
      }
    
    if(element instanceof PlatformFilter){
      PlatformFilter pattern = (PlatformFilter) element;
      string = pattern.getEcuPlatformName();
      }
    
      if (matchText(string)) {
        // if the text matches with pattern
        returnVal = true;
      }
      return returnVal;
  }

}
