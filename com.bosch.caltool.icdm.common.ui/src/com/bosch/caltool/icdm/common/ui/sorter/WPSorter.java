/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.sorter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;

/**
 * @author nip4cob
 */
public class WPSorter extends ViewerComparator {

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final Viewer viewer, final Object wp1, final Object wp2) {
    if ((wp1 instanceof A2lWorkPackage) && (wp2 instanceof A2lWorkPackage)) {
      // get the objects to be compared
      A2lWorkPackage wpObj1 = (A2lWorkPackage) wp1;
      A2lWorkPackage wpObj2 = (A2lWorkPackage) wp2;
      // compare the wp's name
      return ApicUtil.compare(wpObj1.getName(), wpObj2.getName());
    }
    return 0;
  }

}
