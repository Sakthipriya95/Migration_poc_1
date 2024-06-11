/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.text.ParseException;

import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.AttrDiffType;

/**
 * This class handles the common filters for the PID history table viewer
 */
public class PIDCHistoryFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    boolean flag = false;
    // Check if instance of AttrDiffType
    if (element instanceof AttrDiffType) {
      final AttrDiffType attribute = (AttrDiffType) element;
      // Check each element ,if matching
      flag = checkText(attribute);
    }
    return flag;
  }

  /**
   * @param attribute attribute
   * @return True if the properties are matching.
   */
  private boolean checkText(final AttrDiffType attribute) {
    boolean flag = false;
    // Match the Text with the AttrDiffType properties. If any one matches return true.
    if (checkNameVal(attribute) || matchText(attribute.getChangedItem()) || checkModifications(attribute)) {
      flag = true;
    }
    return flag;
  }

  /**
   * @param attribute
   * @return
   */
  private boolean checkNameVal(final AttrDiffType attribute) {
    return matchText(String.valueOf(attribute.getVersionId())) ||
        matchText(String.valueOf(attribute.getPidcversion())) || matchText(attribute.getLevel()) ||
        matchText(attribute.getAttribute().getNameEng());
  }

  /**
   * @param attribute
   * @return
   */
  private boolean checkModifications(final AttrDiffType attribute) {
    boolean modification = false;
    try {
      modification = matchText(attribute.getOldValue()) || matchText(attribute.getNewValue()) ||
          matchText(attribute.getModifiedName()) || matchText(ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15,
              attribute.getModifiedDate(), DateFormat.DATE_FORMAT_08));
      return modification;
    }
    catch (ParseException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return modification;
  }
}