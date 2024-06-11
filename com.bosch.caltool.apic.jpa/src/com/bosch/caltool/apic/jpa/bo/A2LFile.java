package com.bosch.caltool.apic.jpa.bo;

import java.sql.Timestamp;
import java.util.Calendar;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents A2LFile
 */
@Deprecated
public class A2LFile extends ApicObject implements Comparable<A2LFile>, ILinkableObject {


  private PIDCA2l pidcA2l;


  /**
   * @param pidcA2l the pidcA2l to set
   */
  public void setPidcA2l(final PIDCA2l pidcA2l) {
    this.pidcA2l = pidcA2l;
  }


  /**
   * @return the pidcA2l
   */
  public PIDCA2l getPidcA2l() {
    return this.pidcA2l;
  }


  /**
   * Constructor
   *
   * @param apicDataProvider apic Data Provider
   * @param a2lFileID a2l File ID
   * @param pidcID pidc ID
   */
  public A2LFile(final ApicDataProvider apicDataProvider, final Long a2lFileID) {
    super(apicDataProvider, a2lFileID);
  }


  /**
   * @return A2LFile ID
   */
  public Long getA2LFileID() {
    return getID();
  }

  /**
   * @return VCDM A2L file ID
   */
  public Long getVCDMA2LFileID() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getVcdmA2lfileId();
  }

  /**
   * Get Effective A2L File name
   *
   * @return Effective A2L File name
   */
  public String getA2LFileName() {
    String name = getInternalFileName();
    // ICDM-1843
    if ((this.pidcA2l != null) && (this.pidcA2l.getVcdmA2lName() != null)) {
      name = this.pidcA2l.getVcdmA2lName();
    }
    return name;
  }

  /**
   * @return Internal A2L File name
   */
  public String getInternalFileName() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getFilename();
  }

  /**
   * returns the A2lFileName from the TABVA2LFILEINFO
   *
   * @return String
   */
  public String getA2LFileNameFromA2lFileInfo() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getFilename();
  }

  /**
   * @return SDOM PVER Name
   */
  public String getSdomPverName() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getSdomPverName();
  }

  /**
   * @return SDOM PVER revision
   */
  public Long getSdomPverRevision() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getSdomPverRevision();
  }

  /**
   * @return SDOM PVER Variant
   */
  public String getSdomPverVariant() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getSdomPverVariant();
  }

  /**
   * @return SDOM PVER Version ID
   */
  public Long getSdomPverVersionID() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getSdomPverVersid().longValue();
  }

  /**
   * @return A2l Checksum
   */
  public String getA2LFileCheckSum() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getA2lfilechecksum();
  }

  /**
   * This method returns the a2l date in UI format
   *
   * @return date, String
   */
  // ICDM-1671
  public String getA2lFileDateStr() {
    Calendar a2lFileDate = getA2lFileDate();
    String createdDate = "";
    if (null != a2lFileDate) {
      createdDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_11, a2lFileDate);
    }
    return createdDate;
  }

  /**
   * This method returns the a2l date in UI format
   *
   * @return date, String
   */
  // ICDM-1671
  public String getInternalFileDateForUI() {
    Calendar internalFileDate = getInternalFileDate();
    String internalFileDateStr = "";
    if (null != internalFileDate) {
      internalFileDateStr = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_11, internalFileDate);
    }
    return internalFileDateStr;
  }

  /**
   * This method checks if pidc a2l record available for the a2l file and returns the vcdm a2l date
   *
   * @return date, Calendar
   */
  // ICDM-1671
  public Calendar getA2lFileDate() {
    Calendar fileDate;
    if ((null == this.pidcA2l) || (null == this.pidcA2l.getVcdmA2lDate())) {
      fileDate = getInternalFileDate();
    }
    else {
      fileDate = this.pidcA2l.getVcdmA2lDate();
    }
    return fileDate;
  }

  /**
   * This method returns the File date in Calendar format
   *
   * @return A2l created date, String
   */
  // ICDM-1671
  private Calendar getInternalFileDate() {
    Calendar calendar = null;
    Timestamp filedateInDate = getInternalFileDateInTimeStampType();// Fri Nov 28 00:00:00 IST 2014
    if (null != filedateInDate) {
      calendar = ApicUtil.timestamp2calendar(filedateInDate, false);
    }

    return calendar;
  }

  /**
   * This method returns the date from entity "TA2l_FileInfo" Date type
   *
   * @return date String
   */
  // ICDM-1671
  private Timestamp getInternalFileDateInTimeStampType() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getFiledate();
  }

  /**
   * @return Label for A2L file
   */
  public String getLabel() {
    return getSdomPverVariant() + " : " + getA2LFileName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LFile arg0) {
    int result;

    // first compare the SDOM PVER Version
    result = ApicUtil.compare(getSdomPverVariant(), arg0.getSdomPverVariant());

    if (result == 0) {
      // the SDOM PVER Version is equal, compare the file name
      result = ApicUtil.compare(getA2LFileName(), arg0.getA2LFileName());
    }

    // default sort order is decending (ICDM-141)
    return result * -1;
  }

  /**
   * Compare A2LFile object, using the file ID and PIDC ID
   *
   * @param a2lFile a2lFile obj to be compared
   * @return true if objects are same
   */
  // iCDM-514
  public boolean isSameAs(final A2LFile a2lFile) {
    if ((getA2LFileID().longValue() == a2lFile.getA2LFileID().longValue()) &&
        (((this.pidcA2l != null) && (a2lFile.pidcA2l != null)) &&
            (this.pidcA2l.getID().longValue() == a2lFile.pidcA2l.getID().longValue()))) {
      return true;
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getA2LFileName();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return null;
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
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = (ApicConstants.HASH_CODE_PRIME * result) + ((this.pidcA2l == null) ? 0 : this.pidcA2l.getID().hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    // Not applicable
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    // Not applicable
    return null;
  }

  /**
   * @return the path of A2L file in the PIDC Tree
   */
  // ICDM-1649
  public String getExtendedPath() {
    return getPidcA2l().getPIDCard().getActiveVersion().getPidcVersionPath() + getPidcA2l().getPidcVersion().getName() +
        "->" + getSdomPverName() + "->";
  }

  /**
   * @return number of compliance parameters
   */
  public Long getNumCompli() {
    return getEntityProvider().getDbA2LFileInfo(getID()).getNumCompli();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    long compliNum = CommonUtils.isNull(getNumCompli()) ? 0 : getNumCompli();
    return super.getToolTip() + "\nCompliance paramter(s) : " + compliNum;
  }
}
