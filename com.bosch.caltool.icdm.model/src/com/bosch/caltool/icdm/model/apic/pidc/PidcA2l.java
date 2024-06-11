package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.datamodel.core.IMasterRefreshable;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * PidcA2l Model class
 *
 * @author pdh2cob
 */
public class PidcA2l implements Comparable<PidcA2l>, IDataObject, IMasterRefreshable {

  /**
   *
   */
  private static final long serialVersionUID = -6456028770326680375L;
  /**
   * Pidc A2l Id
   */
  private Long id;
  /**
   * Pidc A2l name
   */
  private String name;
  /**
   * Project Id
   */
  private Long projectId;
  /**
   * Pidc Vers Id
   */
  private Long pidcVersId;
  /**
   * A2l File Id
   */
  private Long a2lFileId;
  /**
   * Sdom Pver Name
   */
  private String sdomPverName;
  /**
   * Sdom Pver Name
   */
  private String sdomPverVarName;
  /**
   * Sdom Pver revision
   */
  private Long sdomPverRevision;

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
   * Pidc a2l assigned date
   */
  private String assignedDate;

  /**
   * Pidc a2l assigned user
   */
  private String assignedUser;
  /**
   * Version
   */
  private Long version;
  /**
   * Vcdm A2l Name
   */
  private String vcdmA2lName;
  /**
   * Vcdm A2l Date
   */
  private String vcdmA2lDate;
  /**
   * Ssd Software Version
   */
  private String ssdSoftwareVersion;
  /**
   * Ssd Software Version Id
   */
  private Long ssdSoftwareVersionId;
  /**
   * Ssd Software Proj Id
   */
  private Long ssdSoftwareProjId;
  /**
   * Number of compliance parameters
   */
  private Long compliParamCount;

  /**
   * Indicate if a2l is active. if active it is displayed in pidc tree
   */
  private boolean active;


  /**
   * if true - 'Allow modififying responsiblities at label level' is checked
   */
  private boolean wpParamPresentFlag;


  /**
   * if true - An active wp defn version is present for which 'Allow modififying responsiblities at label level' is
   * checked
   */
  private boolean activeWpParamPresentFlag;
  /**
   * Indicator if working set is modified
   */
  private boolean isWorkingSetModified;

  private boolean masterRefreshApplicable;

  /**
   * Pidc a2l description
   */
  private String description;

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
   * @return projectId
   */
  public Long getProjectId() {
    return this.projectId;
  }

  /**
   * @param projectId set projectId
   */
  public void setProjectId(final Long projectId) {
    this.projectId = projectId;
  }

  /**
   * @return pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId set pidcVersId
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  /**
   * @return a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }

  /**
   * @param a2lFileId set a2lFileId
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }

  /**
   * @return sdomPverName
   */
  public String getSdomPverName() {
    return this.sdomPverName;
  }

  /**
   * @param sdomPverName set sdomPverName
   */
  public void setSdomPverName(final String sdomPverName) {
    this.sdomPverName = sdomPverName;
  }

  /**
   * @return createdDate
   */
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return createdUser
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return modifiedDate
   */
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
   */
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
   * @return vcdmA2lName
   */
  public String getVcdmA2lName() {
    return this.vcdmA2lName;
  }

  /**
   * @param vcdmA2lName set vcdmA2lName
   */
  public void setVcdmA2lName(final String vcdmA2lName) {
    this.vcdmA2lName = vcdmA2lName;
  }

  /**
   * @return vcdmA2lDate
   */
  public String getVcdmA2lDate() {
    return this.vcdmA2lDate;
  }

  /**
   * @param vcdmA2lDate set vcdmA2lDate
   */
  public void setVcdmA2lDate(final String vcdmA2lDate) {
    this.vcdmA2lDate = vcdmA2lDate;
  }

  /**
   * @return ssdSoftwareVersion
   */
  public String getSsdSoftwareVersion() {
    return this.ssdSoftwareVersion;
  }

  /**
   * @param ssdSoftwareVersion set ssdSoftwareVersion
   */
  public void setSsdSoftwareVersion(final String ssdSoftwareVersion) {
    this.ssdSoftwareVersion = ssdSoftwareVersion;
  }

  /**
   * @return ssdSoftwareVersionId
   */
  public Long getSsdSoftwareVersionId() {
    return this.ssdSoftwareVersionId;
  }

  /**
   * @param ssdSoftwareVersionId set ssdSoftwareVersionId
   */
  public void setSsdSoftwareVersionId(final Long ssdSoftwareVersionId) {
    this.ssdSoftwareVersionId = ssdSoftwareVersionId;
  }

  /**
   * @return ssdSoftwareProjId
   */
  public Long getSsdSoftwareProjId() {
    return this.ssdSoftwareProjId;
  }

  /**
   * @param ssdSoftwareProjId set ssdSoftwareProjId
   */
  public void setSsdSoftwareProjId(final Long ssdSoftwareProjId) {
    this.ssdSoftwareProjId = ssdSoftwareProjId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcA2l object) {
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
    if (obj.getClass() == this.getClass()) {
      if (getId() != null) {
        return ModelUtil.isEqual(getId(), ((PidcA2l) obj).getId());
      }
      return ModelUtil.isEqual(getA2lFileId(), ((PidcA2l) obj).getA2lFileId());
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * @return the compliParamCount
   */
  public Long getCompliParamCount() {
    return this.compliParamCount;
  }

  /**
   * @param compliParamCount the compliParamCount to set
   */
  public void setCompliParamCount(final Long compliParamCount) {
    this.compliParamCount = compliParamCount;
  }

  /**
   * @return the sdomPverVarName
   */
  public String getSdomPverVarName() {
    return this.sdomPverVarName;
  }

  /**
   * @param sdomPverVarName the sdomPverVarName to set
   */
  public void setSdomPverVarName(final String sdomPverVarName) {
    this.sdomPverVarName = sdomPverVarName;
  }


  /**
   * @return the sdomPverRevision
   */
  public Long getSdomPverRevision() {
    return this.sdomPverRevision;
  }


  /**
   * @param sdomPverRevision the sdomPverRevision to set
   */
  public void setSdomPverRevision(final Long sdomPverRevision) {
    this.sdomPverRevision = sdomPverRevision;
  }

  /**
   * @return the active
   */
  public boolean isActive() {
    return this.active;
  }

  /**
   * @param active the active to set
   */
  public void setActive(final boolean active) {
    this.active = active;
  }

  /**
   * @return the wpParamPresentFlag
   */
  public boolean isWpParamPresentFlag() {
    return this.wpParamPresentFlag;
  }


  /**
   * @param wpParamPresentFlag the wpParamPresentFlag to set
   */
  public void setWpParamPresentFlag(final boolean wpParamPresentFlag) {
    this.wpParamPresentFlag = wpParamPresentFlag;
  }


  /**
   * @return the activeWpParamPresentFlag
   */
  public boolean isActiveWpParamPresentFlag() {
    return this.activeWpParamPresentFlag;
  }


  /**
   * @param activeWpParamPresentFlag the activeWpParamPresentFlag to set
   */
  public void setActiveWpParamPresentFlag(final boolean activeWpParamPresentFlag) {
    this.activeWpParamPresentFlag = activeWpParamPresentFlag;
  }

  /**
   * @return the isWorkingSetModified
   */
  public boolean isWorkingSetModified() {
    return this.isWorkingSetModified;
  }


  /**
   * @param isWorkingSetModified the isWorkingSetModified to set
   */
  public void setWorkingSetModified(final boolean isWorkingSetModified) {
    this.isWorkingSetModified = isWorkingSetModified;
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
   * @return the assignedDate
   */
  public String getAssignedDate() {
    return this.assignedDate;
  }

  /**
   * @param assignedDate the assignedDate to set
   */
  public void setAssignedDate(final String assignedDate) {
    this.assignedDate = assignedDate;
  }

  /**
   * @return the assignedUser
   */
  public String getAssignedUser() {
    return this.assignedUser;
  }

  /**
   * @param assignedUser the assignedUser to set
   */
  public void setAssignedUser(final String assignedUser) {
    this.assignedUser = assignedUser;
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

}
