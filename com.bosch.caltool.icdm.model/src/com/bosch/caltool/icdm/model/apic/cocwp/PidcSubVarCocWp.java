package com.bosch.caltool.icdm.model.apic.cocwp;

import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * PidcVersCocWp Model class
 *
 * @author UKT1COB
 */
public class PidcSubVarCocWp implements IProjectCoCWP {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 318961343259125L;
  /**
   * Pidc Sub Var Coc Wp Id
   */
  private Long id;
  /**
   * Pidc Sub-Variant Id
   */
  private Long pidcSubVarId;
  /**
   * Wp Id
   */
  private Long wpDivId;

  /**
   * Flag to indicate if WP Division is deleted
   */
  private boolean deleted;
  /**
   * Wp name
   */
  private String name;
  /**
   * Wp description
   */
  private String description;
  /**
   * Used Flag - Y(relevant), N (Not Relevant), ? - Un-Defined
   */
  private String usedFlag;
  /**
   * Is At Child Level
   */
  private boolean atChildLevel;
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
   * @return the deleted
   */
  @Override
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  @Override
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
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
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.id = objId;
  }

  /**
   * @return the pidcSubVarId
   */
  public Long getPidcSubVarId() {
    return this.pidcSubVarId;
  }

  /**
   * @param pidcSubVarId the pidcSubVarId to set
   */
  public void setPidcSubVarId(final Long pidcSubVarId) {
    this.pidcSubVarId = pidcSubVarId;
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
   * {@inheritDoc}
   */
  @Override
  public String getUsedFlag() {
    return this.usedFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setUsedFlag(final String usedFlag) {
    this.usedFlag = usedFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getWPDivId() {
    return this.wpDivId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setWPDivId(final Long wpDivId) {
    this.wpDivId = wpDivId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAtChildLevel() {
    return this.atChildLevel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAtChildLevel(final boolean atChildLevel) {
    this.atChildLevel = atChildLevel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IProjectCoCWP projectCoCWP, final int sortColumn) {
    // NA
    return 0;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final IProjectCoCWP object) {
    int compareResult = 0;
    if (object instanceof PidcSubVarCocWp) {
      compareResult = ModelUtil.compare(getName(), ((PidcSubVarCocWp) object).getName());
      if (compareResult == 0) {
        compareResult = ModelUtil.compare(getId(), ((PidcSubVarCocWp) object).getWPDivId());
      }
    }
    return compareResult;
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
    PidcSubVarCocWp other = (PidcSubVarCocWp) obj;
    if (getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else if (!getName().equals(other.getName())) {
      return false;
    }
    else {
      if (getId() == null) {
        if (other.getId() != null) {
          return false;
        }
      }
      else if (!getId().equals(other.getId())) {
        return false;
      }
    }
    return true;

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
  public PidcSubVarCocWp clone() {
    PidcSubVarCocWp subVarCocWp = null;
    try {
      subVarCocWp = (PidcSubVarCocWp) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return subVarCocWp;
  }
}
