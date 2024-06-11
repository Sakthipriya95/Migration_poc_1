package com.bosch.caltool.icdm.model.apic;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * Alias Detail Model class
 *
 * @author bne4cob
 */
public class AliasDetail implements Cloneable, Comparable<AliasDetail>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 307205258907095L;
  /**
   * Alias Details ID
   */
  private Long id;

  private String name;
  private String description;
  /**
   * Alias Definition ID
   */
  private Long adId;
  /**
   * Attribute ID
   */
  private Long attrId;
  /**
   * Value ID
   */
  private Long valueId;
  /**
   * Alias Name
   */
  private String aliasName;
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
   * @return adId
   */
  public Long getAdId() {
    return this.adId;
  }

  /**
   * @param adId set adId
   */
  public void setAdId(final Long adId) {
    this.adId = adId;
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
   * @return valueId
   */
  public Long getValueId() {
    return this.valueId;
  }

  /**
   * @param valueId set valueId
   */
  public void setValueId(final Long valueId) {
    this.valueId = valueId;
  }

  /**
   * @return aliasName
   */
  public String getAliasName() {
    return this.aliasName;
  }

  /**
   * @param aliasName set aliasName
   */
  public void setAliasName(final String aliasName) {
    this.aliasName = aliasName;
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
  public AliasDetail clone() {
    AliasDetail aliasDetail = null;
    try {
      aliasDetail = (AliasDetail) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return aliasDetail;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final AliasDetail object) {
    int ret = ModelUtil.compare(getName(), object.getName());
    return ret == 0 ? ModelUtil.compare(getId(), object.getId()) : ret;
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((AliasDetail) obj).getId());
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
  public String toString() {
    return super.toString() + " [id=" + this.id + ", name=" + this.name + ", description=" + this.description +
        ", adId=" + this.adId + ", attrId=" + this.attrId + ", valueId=" + this.valueId + ", aliasName=" +
        this.aliasName + ", createdUser=" + this.createdUser + ", createdDate=" + this.createdDate + ", modifiedDate=" +
        this.modifiedDate + ", modifiedUser=" + this.modifiedUser + ", version=" + this.version + "]";
  }

}
