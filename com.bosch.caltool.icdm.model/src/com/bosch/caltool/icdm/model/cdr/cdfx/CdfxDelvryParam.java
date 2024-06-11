package com.bosch.caltool.icdm.model.cdr.cdfx;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * CDFx Delivery Parameter Model class
 *
 * @author pdh2cob
 */
public class CdfxDelvryParam implements Cloneable, Comparable<CdfxDelvryParam>, IDataObject {

  /**
   * Serial UID
   */
  private static final long serialVersionUID = 151658766875534L;
  /**
   * Cdfx Delvry Param Id
   */
  private Long cdfxDelvryParamId;
  /**
   * Cdfx Delvry Wp Resp Id
   */
  private Long cdfxDelvryWpRespId;
  /**
   * Param Id
   */
  private Long paramId;
  /**
   * Rvw Result Id
   */
  private Long rvwResultId;
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
   * @return cdfxDelvryParamId
   */
  public Long getCdfxDelvryParamId() {
    return this.cdfxDelvryParamId;
  }

  /**
   * @param cdfxDelvryParamId set cdfxDelvryParamId
   */
  public void setCdfxDelvryParamId(final Long cdfxDelvryParamId) {
    this.cdfxDelvryParamId = cdfxDelvryParamId;
  }

  /**
   * @return cdfxDelvryWpRespId
   */
  public Long getCdfxDelvryWpRespId() {
    return this.cdfxDelvryWpRespId;
  }

  /**
   * @param cdfxDelvryWpRespId set cdfxDelvryWpRespId
   */
  public void setCdfxDelvryWpRespId(final Long cdfxDelvryWpRespId) {
    this.cdfxDelvryWpRespId = cdfxDelvryWpRespId;
  }


  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }


  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }

  /**
   * @return rvwResultId
   */
  public Long getRvwResultId() {
    return this.rvwResultId;
  }

  /**
   * @param rvwResultId set rvwResultId
   */
  public void setRvwResultId(final Long rvwResultId) {
    this.rvwResultId = rvwResultId;
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
  public CdfxDelvryParam clone() {
    try {
      return (CdfxDelvryParam) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CdfxDelvryParam object) {
    return ModelUtil.compare(getId(), object.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return (obj instanceof CdfxDelvryParam) && ModelUtil.isEqual(getId(), ((CdfxDelvryParam) obj).getId());
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
    return this.cdfxDelvryParamId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    this.cdfxDelvryParamId = objId;
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

}
