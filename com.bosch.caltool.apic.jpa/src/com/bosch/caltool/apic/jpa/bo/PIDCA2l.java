/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;


/**
 * @author bru2cob
 */
@Deprecated
public class PIDCA2l extends ApicObject implements Comparable<PIDCA2l> {

  /**
   * Instance of ApicDataprovider
   */
  private final ApicDataProvider dataProvider;
  /**
   * Key name for PIDC revision field in object details
   */
  public static final String FLD_PIDC_A2L_NAME = "PIDC_REVISION";

  /**
   * Key name for PIDC version name field in object details
   */
  public static final String FLD_PIDC_VRSN_NAME = "FLD_PIDC_VRSN_NAME";


  /**
   * Create new instance of PIDC A2L
   *
   * @param apicDataProvider Data provider
   * @param pidcA2lID Primary Key
   */
  public PIDCA2l(final ApicDataProvider apicDataProvider, final Long pidcA2lID) {
    super(apicDataProvider, pidcA2lID);
    this.dataProvider = apicDataProvider;

    this.dataProvider.getDataCache().getAllPidcA2lMap().put(pidcA2lID, this);
  }

  /**
   * @return ID of PIDC A2L mapping
   * @deprecated use getID() instead
   */
  @Deprecated
  public Long getPidcA2lId() {
    return getID();
  }

  /**
   * @return PIDC instance
   */
  public PIDCVersion getPidcVersion() {

    TPidcVersion tPidcVersion = getEntityProvider().getDbPIDCA2l(getID()).getTPidcVersion();
    if (null != tPidcVersion) {
      try {
        return getDataCache().getPidcVersion(tPidcVersion.getPidcVersId(), true);
      }
      catch (DataException exp) {
        getLogger().error(exp.getMessage(), exp);
      }
    }
    return null;
  }

  // ICDM-1484
  /**
   * @return PIDC instance
   */
  public PIDCard getPIDCard() {
    long pidcID = getEntityProvider().getDbPIDCA2l(getID()).getTabvProjectidcard().getProjectId();
    return getDataCache().getPidc(pidcID);
  }


  /**
   * @return the ssd software version
   */
  public String getSsdSoftwareVersion() {
    return getEntityProvider().getDbPIDCA2l(getID()).getSsdSoftwareVersion();
  }


  /**
   * @return the ssd software version
   */
  public Long getSsdSoftwareVersionID() {
    return getEntityProvider().getDbPIDCA2l(getID()).getSsdSoftwareVersionID();
  }

  /**
   * @return the ssd software proj id
   */
  public Long getSsdSoftwareProjID() {
    return getEntityProvider().getDbPIDCA2l(getID()).getSsdSoftwareProjID();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.PIDC_A2L;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return summary of data
   */
  @Override
  public Map<String, String> getObjectDetails() {
    ConcurrentMap<String, String> summaryMap = new ConcurrentHashMap<String, String>();
    String versionName = "";
    if (null != getPidcVersion()) {
      versionName = getPidcVersion().getPidcVersionName();
    }
    summaryMap.put(FLD_PIDC_VRSN_NAME, versionName);

    return summaryMap;
  }

  /**
   * @return Created Date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDCA2l(getID()).getCreatedDate());
  }

  /**
   * @return Modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDCA2l(getID()).getModifiedDate());
  }

  /**
   * @return Created User
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbPIDCA2l(getID()).getCreatedUser();
  }

  /**
   * @return Modified User
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPIDCA2l(getID()).getModifiedUser();
  }

  /**
   * ICDM-1842
   *
   * @return Original A2lFile Name from VCDM assigned by user
   */
  public String getVcdmA2lName() {
    return getEntityProvider().getDbPIDCA2l(getID()).getVcdmA2lName();
  }

  /**
   * ICDM-1842
   *
   * @return a2lFile Date from VCDM assigned by user
   */
  public Calendar getVcdmA2lDate() {
    // ICDM-1651
    // Since getEntityProvider().getDbPIDCA2l(getID()).getVcdmA2lDate() returns "java.sql.TimeStamp" and
    // any "java.sql.*" object should not be used in the UI layer (to implement MVC pattern effectively).
    // Hence the "java.sql.TimeStamp" object is converted into "java.util.Calendar"
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDCA2l(getID()).getVcdmA2lDate(), false);
  }

  /**
   * @return user who did the latest mapping
   */
  public String getAssignedUserName() {
    if (null == getModifiedUser()) {
      return this.dataProvider.getApicUser(getCreatedUser()).getDisplayName().trim();
    }
    if (this.dataProvider.getApicUser(getModifiedUser()) != null) {
      return this.dataProvider.getApicUser(getModifiedUser()).getDisplayName().trim();
    }
    return "";
  }

  /**
   * @return date when the last mapping is done
   */
  public String getAssignedDateStr() {
    Calendar assignedDate = getAssignedDate();
    return assignedDate.getTime().toString();
  }

  /**
   * @return
   */
  public Calendar getAssignedDate() {
    Calendar assignedDate = getModifiedDate();
    if (null == getModifiedDate()) {
      assignedDate = getCreatedDate();
    }
    return assignedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCA2l arg0) {
    return ApicUtil.compareLong(getID(), arg0.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return a2l file id
   */
  public Long getA2LId() {
    return getEntityProvider().getDbPIDCA2l(getID()).getMvTa2lFileinfo().getId();
  }

  /**
   * @return a2l file name
   */
  public String getA2LFileName() {
    return getEntityProvider().getDbPIDCA2l(getID()).getMvTa2lFileinfo().getFilename();
  }

  /**
   * @return SDOM PVER name
   */
  public String getSdomPverName() {
    return getEntityProvider().getDbPIDCA2l(getID()).getSdomPverName();
  }
}
