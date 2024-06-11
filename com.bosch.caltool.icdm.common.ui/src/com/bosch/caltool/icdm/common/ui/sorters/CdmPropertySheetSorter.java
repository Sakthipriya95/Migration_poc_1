/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorters;

import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetSorter;

/**
 * Custom sorter for Property Sheet. This removes the automatic alphabetical sorting
 * 
 * @author bne4cob
 */
public class CdmPropertySheetSorter extends PropertySheetSorter {

  /**
   * Disabled
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final int compare(final IPropertySheetEntry entryA, final IPropertySheetEntry entryB) {
    return 0;
  }

  /**
   * Disabled
   * <p>
   * {@inheritDoc}
   */
  @Override
  public final int compareCategories(final String categoryA, final String categoryB) {
    return 0;
  }

}
