package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.Objects;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * RvwQnaireRespVariant Model class
 *
 * @author say8cob
 */
public class RvwQnaireRespVariant implements Cloneable, IModel {

  /**
   *
   */
  private static final long serialVersionUID = -1868777703952276157L;
  /**
   * Qnaire Resp Var Id
   */
  private Long id;
  /**
   * Pidc Vers Id
   */
  private Long pidcVersId;
  /**
   * Variant Id
   */
  private Long variantId;
  /**
   * A2l Wp Id
   */
  private Long a2lWpId;

  /**
   * A2l Resp Id
   */
  private Long a2lRespId;
  /**
   * Qnaire Resp Id
   */
  private Long qnaireRespId;
  /**
   * Created Date
   */
  private String createdDate;
  /**
   * Created User
   */
  private String createdUser;
  /**
   * Modified User
   */
  private String modifiedUser;
  /**
   * Modified Date
   */
  private String modifiedDate;
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
   * @return qnaireRespId
   */
  public Long getQnaireRespId() {
    return this.qnaireRespId;
  }

  /**
   * @param qnaireRespId set qnaireRespId
   */
  public void setQnaireRespId(final Long qnaireRespId) {
    this.qnaireRespId = qnaireRespId;
  }


  /**
   * @return the a2lWpId
   */
  public Long getA2lWpId() {
    return this.a2lWpId;
  }


  /**
   * @param a2lWpId the a2lWpId to set
   */
  public void setA2lWpId(final Long a2lWpId) {
    this.a2lWpId = a2lWpId;
  }


  /**
   * @return the a2lRespId
   */
  public Long getA2lRespId() {
    return this.a2lRespId;
  }


  /**
   * @param a2lRespId the a2lRespId to set
   */
  public void setA2lRespId(final Long a2lRespId) {
    this.a2lRespId = a2lRespId;
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
  public RvwQnaireRespVariant clone() {
    RvwQnaireRespVariant rvwQnaireRespVariant = null;
    try {
      rvwQnaireRespVariant = (RvwQnaireRespVariant) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return rvwQnaireRespVariant;
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
    RvwQnaireRespVariant other = (RvwQnaireRespVariant) obj;
    return Objects.equals(getId(), other.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

}
