/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;


/**
 * @author jvi6cob
 */
public class PIDCImportAttrFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean isFilterMatch = false;
    if (element instanceof ProjectImportAttr) {
      final ProjectImportAttr pidcImprtCompRes = (ProjectImportAttr) element;
      if (checkIfFilterMatches(pidcImprtCompRes)) {
        isFilterMatch = true;
      }

    }
    return isFilterMatch;
  }

  /**
   * @param pidcImprtCompRes
   * @return
   */
  private boolean checkIfFilterMatches(final ProjectImportAttr pidcImprtCompRes) {
    String diffText;
    if (pidcImprtCompRes.isNewlyAddedVal()) {
      diffText = "NEW VALUE";
    }
    else {
      diffText = "MODIFIED";
    }
    return checkAttrFields(pidcImprtCompRes,diffText) ||
        checkPidcAttrDetails(pidcImprtCompRes)||
        checkExcelAttrDetails(pidcImprtCompRes)  ;
  }
  
  /**
   * @param pidcImprtCompRes
   * @param diffText 
   * @return
   */
  private boolean checkAttrFields(ProjectImportAttr pidcImprtCompRes, String diffText) {
   return  matchText(pidcImprtCompRes.getAttr().getName()) || matchText(pidcImprtCompRes.getAttr().getDescription()) ||
         matchText(pidcImprtCompRes.getComment())|| matchText(diffText);
  }
  
  /**
   * @param pidcImprtCompRes
   * @return
   */
  private boolean checkPidcAttrDetails(ProjectImportAttr pidcImprtCompRes) {
   return  matchText(pidcImprtCompRes.getPidcAttr().getPartNumber()) ||
       matchText(pidcImprtCompRes.getPidcAttr().getSpecLink()) || 
       checkUsedFlagVal(pidcImprtCompRes.getPidcAttr())||
       matchText(pidcImprtCompRes.getPidcAttr().getAdditionalInfoDesc());
  }
  
  /**
   * @param iProjectAttribute
   * @return
   */
  private boolean checkUsedFlagVal(IProjectAttribute iProjectAttribute) {
    return  matchText(iProjectAttribute.getUsedFlag()) ||
        matchText(iProjectAttribute.getValue());
  }
  
  /**
   * @param pidcImprtCompRes
   * @return
   */
  private boolean checkExcelAttrDetails(ProjectImportAttr pidcImprtCompRes) {
   return checkUsedFlagVal(pidcImprtCompRes.getExcelAttr()) || 
       matchText(pidcImprtCompRes.getExcelAttr().getPartNumber()) ||
       matchText(pidcImprtCompRes.getExcelAttr().getSpecLink())||
       matchText(pidcImprtCompRes.getExcelAttr().getAdditionalInfoDesc());
  }
}