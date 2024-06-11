package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.datamodel.core.IMasterRefreshable;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2lWpDefinitionVersion Model class
 *
 * @author pdh2cob
 */
public class A2lWpDefnVersion implements Cloneable, Comparable<A2lWpDefnVersion>, IDataObject, IMasterRefreshable {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 219137690734615L;
  /**
   * Wp Defn Vers Id
   */
  private Long id;
  /**
   * Version Number
   */
  private Long versionNumber;
  /**
   * Display name of this version
   */
  private String name;

  /**
   * Version Name
   */
  private String versionName;

  /**
   * Version Desc
   */
  private String description;
  /**
   * Is Active
   */
  private boolean isActive;

  /**
   * Is working set version(editable version)
   */
  private boolean workingSet;

  /**
   * Pidc A2l Id
   */
  private Long pidcA2lId;
  /**
   * Param Mapping Allowed
   */
  private boolean paramLevelChgAllowedFlag;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Version
   */
  private Long version;

  /**
   * Is Anytime Active Version - to indicate version is active anytime
   */
  private boolean isAnytimeActiveVersion;

  private boolean masterRefreshApplicable;

  /**
   * VcdmPvdId
   */
  private Long vcdmPvdId;
  /**
   * VcdmPrdId
   */
  private Long vcdmPrdId;


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;
  }

  /**
   * @return versionNumber
   */
  public Long getVersionNumber() {
    return this.versionNumber;
  }

  /**
   * @param versionNumber set versionNumber
   */
  public void setVersionNumber(final Long versionNumber) {
    this.versionNumber = versionNumber;
  }

  /**
   * @return the isActive
   */
  public boolean isActive() {
    return this.isActive;
  }

  /**
   * @param isActive the isActive to set
   */
  public void setActive(final boolean isActive) {
    this.isActive = isActive;
  }

  /**
   * @return the workingSet
   */
  public boolean isWorkingSet() {
    return this.workingSet;
  }

  /**
   * @param workingSet the workingSet to set
   */
  public void setWorkingSet(final boolean workingSet) {
    this.workingSet = workingSet;
  }

  /**
   * @return pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId set pidcA2lId
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }


  /**
   * @return the paramLevelChgAllowedFlag
   */
  public boolean isParamLevelChgAllowedFlag() {
    return this.paramLevelChgAllowedFlag;
  }


  /**
   * @param paramLevelChgAllowedFlag the paramLevelChgAllowedFlag to set
   */
  public void setParamLevelChgAllowedFlag(final boolean paramLevelChgAllowedFlag) {
    this.paramLevelChgAllowedFlag = paramLevelChgAllowedFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public A2lWpDefnVersion clone() {

    A2lWpDefnVersion wpDefn = null;
    try {
      wpDefn = (A2lWpDefnVersion) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return wpDefn;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWpDefnVersion object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWpDefnVersion) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;

  }

  /**
   * @return the versionName
   */
  public String getVersionName() {
    return this.versionName;
  }

  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;

  }

  /**
   * @return the isAnytimeActiveVersion
   */
  public boolean isAnytimeActiveVersion() {
    return this.isAnytimeActiveVersion;
  }

  /**
   * @param isAnytimeActiveVersion the isAnytimeActiveVersion to set
   */
  public void setAnytimeActiveVersion(final boolean isAnytimeActiveVersion) {
    this.isAnytimeActiveVersion = isAnytimeActiveVersion;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isMasterRefreshApplicable() {
    return this.masterRefreshApplicable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setIsMasterRefreshApplicable(final boolean isRefreshApplicable) {
    this.masterRefreshApplicable = isRefreshApplicable;

  }

  /**
   * @return the vcdmPvdId
   */
  public Long getVcdmPvdId() {
    return this.vcdmPvdId;
  }


  /**
   * @param vcdmPvdId the vcdmPvdId to set
   */
  public void setVcdmPvdId(final Long vcdmPvdId) {
    this.vcdmPvdId = vcdmPvdId;
  }


  /**
   * @return the vcdmPrdId
   */
  public Long getVcdmPrdId() {
    return this.vcdmPrdId;
  }


  /**
   * @param vcdmPrdId the vcdmPrdId to set
   */
  public void setVcdmPrdId(final Long vcdmPrdId) {
    this.vcdmPrdId = vcdmPrdId;
  }

}
