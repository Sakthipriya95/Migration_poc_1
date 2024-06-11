package com.bosch.caltool.icdm.model.cdr.cdfx;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * CDFxDelivery Model class
 *
 * @author pdh2cob
 */
public class CDFxDelivery implements Cloneable, Comparable<CDFxDelivery>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 224800624598308L;
  /**
   * Cdfx Delivery Id
   */
  private Long id;
  /**
   * Pidc A2l Id
   */
  private Long pidcA2lId;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * Wp Defn Vers Id
   */
  private Long wpDefnVersId;
  /**
   * Scope
   */
  private String scope;
  /**
   * Readiness Yn
   */
  private String readinessYn;
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
   * Scope
   */
  private String isOneFilePerWpYn;

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
   * @return variantId
   */
  public Long getVariantId() {
    return this.variantId;
  }

  /**
   * @param variantId set variantId
   */
  public void setVariantId(final Long variantId) {
    this.variantId = variantId;
  }

  /**
   * @return wpDefnVersId
   */
  public Long getWpDefnVersId() {
    return this.wpDefnVersId;
  }

  /**
   * @param wpDefnVersId set wpDefnVersId
   */
  public void setWpDefnVersId(final Long wpDefnVersId) {
    this.wpDefnVersId = wpDefnVersId;
  }

  /**
   * @return scope
   */
  public String getScope() {
    return this.scope;
  }

  /**
   * @param scope set scope
   */
  public void setScope(final String scope) {
    this.scope = scope;
  }

  /**
   * @return readinessYn
   */
  public String getReadinessYn() {
    return this.readinessYn;
  }

  /**
   * @param readinessYn set readinessYn
   */
  public void setReadinessYn(final String readinessYn) {
    this.readinessYn = readinessYn;
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
  public CDFxDelivery clone() {
    try {
      return (CDFxDelivery) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDFxDelivery object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return (obj instanceof CDFxDelivery) && ModelUtil.isEqual(getId(), ((CDFxDelivery) obj).getId());
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
    //NA
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    //NA
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    //NA
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    //NA
  }

  /**
   * @return the isOneFilePerWpYn
   */
  public String getIsOneFilePerWpYn() {
    return this.isOneFilePerWpYn;
  }


  /**
   * @param isOneFilePerWpYn the isOneFilePerWpYn to set
   */
  public void setIsOneFilePerWpYn(final String isOneFilePerWpYn) {
    this.isOneFilePerWpYn = isOneFilePerWpYn;
  }
}