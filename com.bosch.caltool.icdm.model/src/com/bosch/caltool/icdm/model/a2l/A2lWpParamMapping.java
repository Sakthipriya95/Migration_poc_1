package com.bosch.caltool.icdm.model.a2l;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * A2lWpParamMapping Model class
 *
 * @author pdh2cob
 */
public class A2lWpParamMapping implements Cloneable, Comparable<A2lWpParamMapping>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 219231045448953L;
  /**
   * Wp Param Mapping Id
   */
  private Long id;
  /**
   * Param Id
   */
  private Long paramId;
  /**
   * Param Name
   */
  private String name;
  /**
   * Param Description
   */
  private String description;
  /**
   * Wp Resp Id
   */
  private Long wpRespId;
  /**
   * Wp Defn Version Id
   */
  // Eventhough WP Definition version is not directly available in the table, it is added in this model, for easy
  // access, CNS refresh etc.
  // The field is not used for create/update operations
  private Long wpDefnVersionId;
  /**
   * WP Name at customer
   */
  private String wpNameCust;

  /**
   * WP Resp Inherited flag
   */
  private boolean wpRespInherited;

  /**
   * WP Name Inherited flag
   */
  private boolean wpNameCustInherited;

  /**
   * ParA2lResId
   */
  private Long parA2lRespId;

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
   * @return paramId
   */
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId set paramId
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }

  /**
   * @return wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }

  /**
   * @param wpRespId set wpRespId
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
  }


  /**
   * @return wpDefnVersionId
   */
  public Long getWpDefnVersionId() {
    return this.wpDefnVersionId;
  }

  /**
   * @param wpDefnVersionId set wpDefnVersionId
   */
  public void setWpDefnVersionId(final Long wpDefnVersionId) {
    this.wpDefnVersionId = wpDefnVersionId;
  }


  /**
   * @return the wpNameCust
   */
  public String getWpNameCust() {
    return this.wpNameCust;
  }


  /**
   * @param wpNameCust the wpNameCust to set
   */
  public void setWpNameCust(final String wpNameCust) {
    this.wpNameCust = wpNameCust;
  }


  /**
   * @return the wpRespInherited
   */
  public boolean isWpRespInherited() {
    return this.wpRespInherited;
  }


  /**
   * @param wpRespInherited the wpRespInherited to set
   */
  public void setWpRespInherited(final boolean wpRespInherited) {
    this.wpRespInherited = wpRespInherited;
  }


  /**
   * @return the wpNameCustInherited
   */
  public boolean isWpNameCustInherited() {
    return this.wpNameCustInherited;
  }


  /**
   * @param wpNameCustInherited the wpNameCustInherited to set
   */
  public void setWpNameCustInherited(final boolean wpNameCustInherited) {
    this.wpNameCustInherited = wpNameCustInherited;
  }


  /**
   * @return the parA2lRespId
   */
  public Long getParA2lRespId() {
    return this.parA2lRespId;
  }


  /**
   * @param parA2lRespId the parA2lRespId to set
   */
  public void setParA2lRespId(final Long parA2lRespId) {
    this.parA2lRespId = parA2lRespId;
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
  public A2lWpParamMapping clone() {
    A2lWpParamMapping mapping = null;
    try {
      mapping = (A2lWpParamMapping) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return mapping;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWpParamMapping object) {
    return ModelUtil.compare(getParamId(), object.getParamId());
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
    return (obj.getClass() == this.getClass()) && ModelUtil.isEqual(getId(), ((A2lWpParamMapping) obj).getId()) &&
        ModelUtil.isEqual(getParamId(), ((A2lWpParamMapping) obj).getParamId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getParamId());
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


}
