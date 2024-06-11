package com.bosch.caltool.icdm.model.cdr.cdfx;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * CDFxDelWpResp Model class
 *
 * @author pdh2cob
 */
public class CDFxDelWpResp implements Cloneable, Comparable<CDFxDelWpResp>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 224862515443783L;
  /**
   * Cdfx Del Wp Resp Id
   */
  private Long cdfxDelWpRespId;
  /**
   * Cdfx Delivery Id
   */
  private Long cdfxDeliveryId;

  /**
   * Wp Id
   */
  private Long wpId;
  /**
   * Resp Id
   */
  private Long respId;
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
   * @return cdfxDelWpRespId
   */
  public Long getCdfxDelWpRespId() {
    return this.cdfxDelWpRespId;
  }

  /**
   * @param cdfxDelWpRespId set cdfxDelWpRespId
   */
  public void setCdfxDelWpRespId(final Long cdfxDelWpRespId) {
    this.cdfxDelWpRespId = cdfxDelWpRespId;
  }

  /**
   * @return cdfxDeliveryId
   */
  public Long getCdfxDeliveryId() {
    return this.cdfxDeliveryId;
  }

  /**
   * @param cdfxDeliveryId set cdfxDeliveryId
   */
  public void setCdfxDeliveryId(final Long cdfxDeliveryId) {
    this.cdfxDeliveryId = cdfxDeliveryId;
  }


  /**
   * @return wpId
   */
  public Long getWpId() {
    return this.wpId;
  }

  /**
   * @param wpId set wpId
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }

  /**
   * @return respId
   */
  public Long getRespId() {
    return this.respId;
  }

  /**
   * @param respId set respId
   */
  public void setRespId(final Long respId) {
    this.respId = respId;
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
  public CDFxDelWpResp clone() {
    try {
      return (CDFxDelWpResp) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDFxDelWpResp object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return (obj instanceof CDFxDelWpResp) && ModelUtil.isEqual(getId(), ((CDFxDelWpResp) obj).getId());
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
  public Long getId() {
    return this.cdfxDelWpRespId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.cdfxDelWpRespId = objId;
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
  public void setName(final String name) {
    // TODO Auto-generated method stub

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
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // TODO Auto-generated method stub

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

}
