/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;


/**
 * Extended class for <code>PropertySheetPage</code> to provide custom sorter
 * 
 * @author bne4cob
 */
public class CdmPropertySheetPage extends PropertySheetPage {

  /**
   * {@inheritDoc}
   * <p>
   * The overridden method in <code>CdmPropertySheetPage</code>has public access.
   */

  public final void setSheetSorter(final PropertySheetSorter sorter) { // NOPMD
    // No changes
    setSorter(sorter);
  }

}
