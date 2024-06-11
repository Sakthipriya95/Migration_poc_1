/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.IBasicObject;
import com.bosch.caltool.icdm.common.util.ApicUtil;

/**
 * BO class for SDOM PVER
 */
public class SdomPver implements Comparable<SdomPver>, IBasicObject {

  /**
   * PVER Name
   */
  private final String pverName;

  /**
   * Parent PIDC Version of this PVER
   */
  private final Long pidcVersID;

  /**
   * data provider
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * PVER Description
   */
  private final String description;


  /**
   * Constructor
   * 
   * @param apicDataProvider
   * @param pVerValue
   * @param pidcVersID
   */
  public SdomPver(final ApicDataProvider apicDataProvider, final AttributeValue pVerValue, final Long pidcVersID) {

    this.apicDataProvider = apicDataProvider;

    this.pverName = pVerValue.getValue();

    this.description = pVerValue.getDescription();

    this.pidcVersID = pidcVersID;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.pverName;
  }

  /**
   * @return PVER Name
   */
  public String getPVERName() {
    return getName();
  }

  /**
   * @return PIDCard
   */
  public PIDCVersion getPIDCVersion() {
    return this.apicDataProvider.getDataCache().getPidcVersion(this.pidcVersID);
  }

  /**
   * Get all A2L file objects available for the given pver name in this PIDC
   * 
   * @return Map. Key - A2L file ID, Value - A2L file object
   */
  protected Map<Long, A2LFile> getAllA2LFileMap() {
    PIDCard pidc = this.apicDataProvider.getPidcVersion(this.pidcVersID).getPidc();
    return this.apicDataProvider.getDataCache().getPverAllA2LMap(pidc.getID(), this.pverName, true);
  }

  /**
   * @return sorted set of A2L files of this PVER
   */
  public SortedSet<A2LFile> getA2LFiles() {
    return new TreeSet<>(getAllA2LFileMap().values());
  }

  /**
   * @return sorted set of mapped a2l files to the PIDC version
   */
  public SortedSet<A2LFile> getMappedA2LFiles() {
    PIDCVersion pidcVer = getPIDCVersion();
    return this.apicDataProvider.getDataLoader().getMappedA2LFiles(this.pverName, pidcVer.getID());
  }

  /**
   * {@inheritDoc} returns compare result of two SDOM PVER
   */
  @Override
  public int compareTo(final SdomPver arg0) {
    int result = ApicUtil.compare(this.pverName, arg0.getPVERName());
    if (result == 0) {
      return ApicUtil.compare(getPIDCVersion(), arg0.getPIDCVersion());
    }
    return result;
  }

  // ICDM-1146
  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * ICDM-1146 Tooltip for the nodes in the pidc tree
   * 
   * @return tooltip text
   */
  @Override
  public String getToolTip() {
    return "PVER Name: " + this.pverName + "\nDescription: " + this.description;
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
    // Pver names occur in many pidc's, check for pverName and pidc also
    SdomPver pverOther = (SdomPver) obj;
    return getPVERName().equals(pverOther.getPVERName()) && getPIDCVersion().equals(pverOther.getPIDCVersion());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
