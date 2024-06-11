/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author gge6cob
 */
public class A2LFileBO implements Comparable<A2LFileBO> {

  private final A2LFile a2lFile;

  /**
   * @param a2lFile
   */
  public A2LFileBO(final A2LFile a2lFile) {
    this.a2lFile = a2lFile;
  }

  /**
   * @return A2LFile ID
   */
  public Long getA2LFileID() {
    return this.a2lFile.getId();
  }

  /**
   * @return VCDM A2L file ID
   */
  public Long getVCDMA2LFileID() {
    return this.a2lFile.getVcdmA2lfileId();
  }


  /**
   * @return Internal A2L File name
   */
  public String getInternalFileName() {
    return this.a2lFile.getFilename();
  }

  /**
   * returns the A2lFileName from the TABVA2LFILEINFO
   *
   * @return String
   */
  public String getA2LFileNameFromA2lFileInfo() {
    return this.a2lFile.getFilename();
  }

  /**
   * @return SDOM PVER Name
   */
  public String getSdomPverName() {
    return this.a2lFile.getSdomPverName();
  }

  /**
   * @return SDOM PVER revision
   */
  public Long getSdomPverRevision() {
    return this.a2lFile.getSdomPverRevision();
  }

  /**
   * @return SDOM PVER Variant
   */
  public String getSdomPverVariant() {
    return this.a2lFile.getSdomPverVariant();
  }

  /**
   * @return SDOM PVER Version ID
   */
  public Long getSdomPverVersionID() {
    return this.a2lFile.getSdomPverVersid().longValue();
  }

  /**
   * @return A2l Checksum
   */
  public String getA2LFileCheckSum() {
    return this.a2lFile.getA2lfilechecksum();
  }


  /**
   * This method returns the File date in Calendar format
   *
   * @return A2l created date, String
   */
  // ICDM-1671
  public String getInternalFileDate() {
    return this.a2lFile.getFiledate() != null ? this.a2lFile.getFiledate() : ApicConstants.EMPTY_STRING;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LFileBO arg0) {
    int result;

    // first compare the SDOM PVER Version
    result = ApicUtil.compare(getSdomPverVariant(), arg0.getSdomPverVariant());

    if (result == 0) {
      // the SDOM PVER Version is equal, compare the file name
      result = ApicUtil.compare(getInternalFileName(), arg0.getInternalFileName());
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
    A2LFile fileOther = (A2LFile) obj;
    // same a2l files occur in different pidcs, check the pidc also
    return isSameAs(fileOther);
  }


  /**
   * Compare A2LFile object, using the file ID and PIDC ID.
   *
   * @param a2lFileObj a2lFileObj
   * @return true if objects are same
   */
  // iCDM-514
  public boolean isSameAs(final A2LFile a2lFileObj) {
    return (this.a2lFile != null) && (getA2LFileID().longValue() == a2lFileObj.getId().longValue());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (ApicConstants.HASH_CODE_PRIME * result) + ((this.a2lFile == null) ? 0 : this.a2lFile.getId().hashCode());
    return result;
  }


  /**
   * @return number of compliance parameters
   */
  public Long getNumCompli() {
    return this.a2lFile.getNumCompli();
  }

  /**
   * {@inheritDoc}
   */
  public String getToolTip() {
    long compliNum = CommonUtils.isNull(getNumCompli()) ? 0 : getNumCompli();
    StringBuilder toolTip = new StringBuilder("Name : ");
    toolTip.append(getInternalFileName());
    return toolTip + "\nCompliance paramter(s) : " + compliNum;
  }
}
