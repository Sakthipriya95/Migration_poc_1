package com.bosch.caltool.icdm.model.uc;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Ucp Attr Model class
 *
 * @author MKL2COB
 */
public class UcpAttr implements Cloneable, Comparable<UcpAttr>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = 8079254661999947844L;

  /**
   * Ucpa Id
   */
  private Long id;

  /**
   * Attribute name
   */
  private String name;
  /**
   * Attribute description
   */
  private String description;

  /**
   * Use Case Id
   */
  private Long useCaseId;
  /**
   * Section Id
   */
  private Long sectionId;
  /**
   * Attr Id
   */
  private Long attrId;

  /**
   * BitSet value to check if uc attribute is quotation relevant 1 - relevant 0 - not relevant
   */
  private Long mappingFlags;

  /**
   * Created User
   */
  private String createdUser;
  /**
   * Created Date
   */
  private String createdDate;
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
   * @return useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }

  /**
   * @param useCaseId set useCaseId
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }

  /**
   * @return sectionId
   */
  public Long getSectionId() {
    return this.sectionId;
  }

  /**
   * @param sectionId set sectionId
   */
  public void setSectionId(final Long sectionId) {
    this.sectionId = sectionId;
  }

  /**
   * @return attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }

  /**
   * @param attrId set attrId
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * @return the name
   */
  @Override
  public String getName() {
    return this.name;
  }


  /**
   * @param name the name to set
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @return the description
   */
  @Override
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * @return createdUser
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * @param createdUser set createdUser
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * @return createdDate
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * @param createdDate set createdDate
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return modifiedDate
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * @param modifiedDate set modifiedDate
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * @return modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * @param modifiedUser set modifiedUser
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
  public UcpAttr clone() {
    try {
      return (UcpAttr) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final UcpAttr object) {
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((UcpAttr) obj).getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }


  /**
   * @return the mappingFlags
   */
  public Long getMappingFlags() {
    return this.mappingFlags;
  }


  /**
   * @param mappingFlags the mappingFlags to set
   */
  public void setMappingFlags(final Long mappingFlags) {
    this.mappingFlags = mappingFlags;
  }


}
