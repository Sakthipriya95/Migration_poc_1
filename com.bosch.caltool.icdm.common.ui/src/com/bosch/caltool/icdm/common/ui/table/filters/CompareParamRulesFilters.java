package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;

/**
 * This class handles the common filters for compare rules page in Caldatafileimport wizard
 */
public class CompareParamRulesFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean flag = false;
    if (element instanceof CalDataImportComparisonModel) {
      // get the compare object
      final CalDataImportComparisonModel compObj = (CalDataImportComparisonModel) element;
      flag = checkText(compObj);
    }
    return flag;

  }

  /**
   * @param compObj attribute
   * @return True if the properties are matching.
   */
  private boolean checkText(final CalDataImportComparisonModel compObj) {
    boolean flag = false;
    // Match the Text . If any one matches return true.
    if (matchText(compObj.getParamName()) || matchText(compObj.getParamType()) ||
        matchText(compObj.getParamDependency()) || matchText(compObj.getFuncNames())) {
      flag = true;
    }
    return flag;
  }
}