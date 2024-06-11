/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.sorters;

import java.util.Comparator;

import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;


/**
 * @author rgo7cob
 */
public class A2lWpDefVersionSorter implements Comparator<A2lWpDefnVersion> {


  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(final A2lWpDefnVersion def1, final A2lWpDefnVersion def2) {

    if (def1.getVersionNumber() == 0) {
      return -1;
    }
    if (def2.getVersionNumber() == 0) {
      return 1;
    }

    if (def1.isActive()) {
      return -1;
    }
    if (def2.isActive()) {
      return 1;
    }


    return def2.getVersionNumber().compareTo(def1.getVersionNumber());
  }


}
