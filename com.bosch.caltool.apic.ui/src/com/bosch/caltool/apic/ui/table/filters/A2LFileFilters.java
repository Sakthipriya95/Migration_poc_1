/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;


/**
 * Filter for the A2lFile page in PIDC Editor
 *
 * @author bru2cob
 */
public class A2LFileFilters extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof PidcA2lFileExt) {
      final PidcA2lFileExt a2lFile = (PidcA2lFileExt) element;
      // check version name
      if (checkVersionName(a2lFile)) {
        return true;
      }
      // check mapping detail
      if (checkMappingDetail(a2lFile)) {
        return true;
      }
      // check a2l details
      if (checkA2lDetails(a2lFile)) {
        return true;
      }
      // Check software version
      if (checkSSDSoftVersion(a2lFile)) {
        return true;
      }

    }
    return false;
  }


  /**
   * @param a2lFile
   * @return
   */
  private boolean checkSSDSoftVersion(final PidcA2lFileExt a2lFile) {
    PidcA2l pidcA2l = a2lFile.getPidcA2l();
    if (pidcA2l != null) {
      String ssdSoftwareVersion = pidcA2l.getSsdSoftwareVersion();
      if ((ssdSoftwareVersion != null) && matchText(ssdSoftwareVersion)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Check pidc version name
   *
   * @param a2lFile
   * @return
   */
  private boolean checkVersionName(final PidcA2lFileExt a2lFile) {
    String versionName = "";
    if ((null != a2lFile.getPidcA2l()) && (null != a2lFile.getPidcVersion())) {
      versionName = a2lFile.getPidcVersion().getVersionName();
    }
    // check the pidc version name
    return matchText(versionName);
  }

  /**
   * Check a2l file details
   *
   * @param pidcA2lExt
   * @return
   */
  private boolean checkA2lDetails(final PidcA2lFileExt pidcA2lExt) {
    // check the a2l file name
    if (matchText(pidcA2lExt.getA2lFile().getFilename())) {
      return true;
    }
    // check the sdom pver name
    if (matchText(pidcA2lExt.getA2lFile().getSdomPverName())) {
      return true;
    }
    // check the pver variant name
    return (matchText(pidcA2lExt.getA2lFile().getSdomPverVariant()));
  }

  /**
   * Check mapping details
   *
   * @param pidcA2lExt
   * @return
   */
  private boolean checkMappingDetail(final PidcA2lFileExt pidcA2lExt) {
    String date = "";
    if (null != pidcA2lExt.getPidcA2l()) {
      date = new PidcA2LBO(pidcA2lExt.getPidcA2l().getId(), pidcA2lExt).getAssignedDate();
    }
    // check date
    if (matchText(date)) {
      return true;
    }
    String name = "";
    if (null != pidcA2lExt.getPidcA2l()) {
      name = new PidcA2LBO(pidcA2lExt.getPidcA2l().getId(), pidcA2lExt).getAssignedUser();
    }
    // check name
    return (matchText(name));
  }
}
