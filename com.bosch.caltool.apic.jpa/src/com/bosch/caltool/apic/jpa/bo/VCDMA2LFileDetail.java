package com.bosch.caltool.apic.jpa.bo;

import java.sql.Timestamp;
import java.util.Calendar;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;

/**
 * ICDM-1841 A wrapper class for the TA2L_VCDM_VERSIONS data fetched for an a2l File.</br>
 * Used for displaying the alternate a2l file names
 *
 * @author jvi6cob
 */
public class VCDMA2LFileDetail implements Comparable<VCDMA2LFileDetail> {

  private String originalFileName;

  /**
   * the original date of vcdm file
   */
  private Timestamp originalDate;
  private String pst;
  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the originalFileName
   */
  public String getOriginalFileName() {
    return this.originalFileName;
  }


  /**
   * @param originalFileName the originalFileName to set
   */
  public void setOriginalFileName(final String originalFileName) {
    this.originalFileName = originalFileName;
  }


  /**
   * @return the originalDate
   */
  public Calendar getOriginalDate() {
    return ApicUtil.timestamp2calendar(this.originalDate, false);
  }


  /**
   * @param originalDate the originalDate to set
   */
  public void setOriginalDate(final Timestamp originalDate) {
    this.originalDate = originalDate;
  }


  /**
   * @return the pst
   */
  public String getPst() {
    return this.pst;
  }


  /**
   * @param pst the pst to set
   */
  public void setPst(final String pst) {
    this.pst = pst;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final VCDMA2LFileDetail otherA2lFileDetails) {
    return ApicUtil.compare(getOriginalFileName(), otherA2lFileDetails.getOriginalFileName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getOriginalFileName() == null) ? 0 : getOriginalFileName().hashCode());
    return result;
  }


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
    VCDMA2LFileDetail other = (VCDMA2LFileDetail) obj;
    return CommonUtils.isEqual(getOriginalFileName(), other.getOriginalFileName());
  }
}