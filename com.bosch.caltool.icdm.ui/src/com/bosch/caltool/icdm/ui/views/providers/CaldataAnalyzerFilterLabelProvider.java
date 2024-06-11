/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerFilterLabelProvider extends ColumnLabelProvider {


  /**
   * {@inheritDoc}
   */
  @Override
  public String getText(final Object element) {
    String name = (String) element;
    return name;
  }


}
