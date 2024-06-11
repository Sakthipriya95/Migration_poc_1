/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.client.bo.cda.CalDataAnalyzerSortingUtil;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerResultFileModel;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author pdh2cob
 */
public class CaldataAnalyzerFilterSorter extends AbstractViewerSorter {

  private int index;
  private static final int DESCENDING = 1;
  private static final int ASCENDING = 0;
  private int direction = ASCENDING;

  private final CalDataAnalyzerSortingUtil cdaSorter = new CalDataAnalyzerSortingUtil();

  /**
   * {@inheritDoc}
   */
  @Override
  public void setColumn(final int index) {
    if (index == this.index) {
      this.direction = 1 - this.direction;
    }
    else {
      this.index = index;
      this.direction = ASCENDING;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getDirection() {
    return this.direction;
  }

  @Override
  public int compare(final Viewer viewer, final Object object1, final Object object2) {

    int compare = 0;

    if ((object1 instanceof ParameterFilterLabel) && (object2 instanceof ParameterFilterLabel)) {
      compare = this.cdaSorter.compareTo((ParameterFilterLabel) object1, (ParameterFilterLabel) object2, this.index);
    }
    if ((object1 instanceof SystemConstantFilter) && (object2 instanceof SystemConstantFilter)) {
      compare = this.cdaSorter.compareTo((SystemConstantFilter) object1, (SystemConstantFilter) object2, this.index);
    }

    if ((object1 instanceof FunctionFilter) && (object2 instanceof FunctionFilter)) {
      compare = this.cdaSorter.compareTo((FunctionFilter) object1, (FunctionFilter) object2, this.index);
    }

    if ((object1 instanceof CustomerFilter) && (object2 instanceof CustomerFilter)) {
      compare = this.cdaSorter.compareTo((CustomerFilter) object1, (CustomerFilter) object2, this.index);
    }

    if ((object1 instanceof PlatformFilter) && (object2 instanceof PlatformFilter)) {
      compare = this.cdaSorter.compareTo((PlatformFilter) object1, (PlatformFilter) object2, this.index);
    }

    if ((object1 instanceof CaldataAnalyzerResultFileModel) && (object2 instanceof CaldataAnalyzerResultFileModel)) {
      compare = this.cdaSorter.compareTo((CaldataAnalyzerResultFileModel) object1,
          (CaldataAnalyzerResultFileModel) object2, this.index);
    }

    // If descending order, flip the direction
    if (this.direction == DESCENDING) {
      compare = -compare;
    }
    return compare;
  }
}
