/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * The Class PidcA2LBO.
 *
 * @author gge6cob
 */
public class PidcA2LBO implements Comparable<PidcA2LBO> {

  /**
   * The pidc A l.
   */
  private PidcA2l pidcA2l;

  /**
   * The a2l file BO.
   */
  private A2LFileBO a2lFileBO;

  /**
   * The pidc A 2 l id.
   */
  private final Long pidcA2lId;

  /**
   * The pidc version.
   */
  private PidcVersion pidcVersion;

  private PidcA2lFileExt pidcA2lExt;

  /**
   * Instantiates a new pidc A 2 LBO.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @param pidcA2lExt the pidc A 2 l ext
   */
  public PidcA2LBO(final Long pidcA2lId, final PidcA2lFileExt pidcA2lExt) {
    this.pidcA2lId = pidcA2lId;
    this.pidcA2lExt = pidcA2lExt;
    loadPidcA2lDetails();
  }

  /**
   * Load pidc A 2 l details.
   */
  private void loadPidcA2lDetails() {
    try {
      if (this.pidcA2lExt == null) {
        this.pidcA2lExt = new PidcA2lServiceClient().getPidcA2LFileDetails(this.pidcA2lId);
      }
      this.pidcA2l = this.pidcA2lExt.getPidcA2l();
      this.a2lFileBO = new A2LFileBO(this.pidcA2lExt.getA2lFile());
      this.pidcVersion = this.pidcA2lExt.getPidcVersion();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error loading PidcA2L related details" + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Gets the pidc A 2 l id.
   *
   * @return the pidc A 2 l id
   */
  public Long getPidcA2lId() {
    return this.pidcA2l.getId();
  }

  /**
   * Gets the SSD software version.
   *
   * @return the ssd software version
   */
  public String getSsdSoftwareVersion() {
    return this.pidcA2l.getSsdSoftwareVersion();
  }


  /**
   * Gets the SSD software version ID.
   *
   * @return the ssd software version
   */
  public Long getSsdSoftwareVersionID() {
    return this.pidcA2l.getSsdSoftwareVersionId();
  }

  /**
   * Gets the SSD software project ID.
   *
   * @return the ssd software proj id
   */
  public Long getSsdSoftwareProjID() {
    return this.pidcA2l.getSsdSoftwareProjId();
  }

  /**
   * Gets the created date.
   *
   * @return Created Date
   */
  public String getCreatedDate() {
    return this.pidcA2l.getCreatedDate();
  }

  /**
   * Gets the modified date.
   *
   * @return Modified date
   */
  public String getModifiedDate() {
    return this.pidcA2l.getModifiedDate();
  }

  /**
   * Gets the created user.
   *
   * @return Created User
   */
  public String getCreatedUser() {
    return this.pidcA2l.getCreatedUser();
  }

  /**
   * Gets the modified user.
   *
   * @return Modified User
   */
  public String getModifiedUser() {
    return this.pidcA2l.getModifiedUser();
  }

  /**
   * @return Original A2lFile Name from VCDM assigned by user
   */
  // ICDM-1842
  public String getVcdmA2lName() {
    return this.pidcA2l.getVcdmA2lName();
  }

  /**
   * @return a2lFile Date from VCDM assigned by user
   */
  // ICDM-1842
  public String getVcdmA2lDate() {
    return this.pidcA2l.getVcdmA2lDate();
  }


  /**
   * @return the assigned user
   */
  public String getAssignedUser() {
    return this.pidcA2l.getAssignedUser();
  }

  /**
   * Gets the assigned date.
   *
   * @return the assigned date
   */
  public String getAssignedDate() {
    return this.pidcA2l.getAssignedDate();

  }


  /**
   * @return the formatted page
   */
  public String getAssignedDateFormatted() {
    String assignedDate = getAssignedDate();
    if (assignedDate != null) {
      SimpleDateFormat dateFormatsource =
          new SimpleDateFormat(DateFormat.DATE_FORMAT_15, Locale.getDefault(Locale.Category.FORMAT));
      Date date = null;

      try {
        date = dateFormatsource.parse(assignedDate);
      }
      catch (ParseException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      SimpleDateFormat dateFormatDest = new SimpleDateFormat(DateFormat.DATE_FORMAT_04);
      return dateFormatDest.format(date);
    }
    return "";

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Gets the sdom pver name.
   *
   * @return SDOM PVER name
   */
  public String getSdomPverName() {
    return this.a2lFileBO.getSdomPverName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcA2LBO arg0) {
    int result;

    // first compare the SDOM PVER Version
    result = ApicUtil.compare(this.a2lFileBO.getSdomPverVariant(), arg0.a2lFileBO.getSdomPverVariant());

    if (result == 0) {
      // the SDOM PVER Version is equal, compare the file name
      result = ApicUtil.compare(getA2LFileName(), arg0.getA2LFileName());
    }

    // default sort order is decending (ICDM-141)
    return result * -1;
  }


  // iCDM-1241
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PidcA2LBO fileOther = (PidcA2LBO) obj;
    // same a2l files occur in different pidcs, check the pidc also

    return ((this.a2lFileBO.getA2LFileID().longValue() == fileOther.a2lFileBO.getA2LFileID().longValue()) &&
        (this.pidcA2l != null));

  }


  /**
   * Get Effective A2L File name.
   *
   * @return Effective A2L File name
   */
  public String getA2LFileName() {
    String name = this.a2lFileBO.getInternalFileName();
    if ((this.pidcA2l != null) && (this.pidcA2l.getVcdmA2lName() != null)) {
      name = getVcdmA2lName();
    }
    return name;
  }


  /**
   * Gets the tool tip.
   *
   * @return the tool tip
   */
  public String getToolTip() {
    long compliNum = CommonUtils.isNull(this.a2lFileBO.getNumCompli()) ? 0 : this.a2lFileBO.getNumCompli();
    StringBuilder toolTip = new StringBuilder("Name : ");
    toolTip.append(getA2LFileName());
    return toolTip + "\nCompliance paramter(s) : " + compliNum;
  }

  /**
   * This method checks if pidc a2l record available for the a2l file and returns the vcdm a2l date
   *
   * @return date, Calendar
   */
  // ICDM-1671
  public String getA2lFileDate() {
    String a2lFileDate =
        this.a2lFileBO.getInternalFileDate() != null ? this.a2lFileBO.getInternalFileDate() : getVcdmA2lDate();
    return a2lFileDate != null ? a2lFileDate : ApicConstants.EMPTY_STRING;
  }

  /**
   * Gets the pidc version.
   *
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * Sets the pidc version.
   *
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return Label for A2L file
   */
  public String getLabel() {
    return this.a2lFileBO.getSdomPverVariant() + " : " + getA2LFileName();
  }


  /**
   * @return the a2lFileBO
   */
  public A2LFileBO getA2lFileBO() {
    return this.a2lFileBO;
  }


  /**
   * @param a2lFileBO the a2lFileBO to set
   */
  public void setA2lFileBO(final A2LFileBO a2lFileBO) {
    this.a2lFileBO = a2lFileBO;
  }

  /**
   * @return the a2lFile
   */
  public A2LFile getA2lFile() {
    return this.pidcA2lExt.getA2lFile();
  }


  /**
   * @return the pidcA2l
   */
  public PidcA2l getPidcA2l() {
    return this.pidcA2l;
  }
}
