package com.bosch.caltool.icdm.model.cdr.qnaire;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * QuestionnaireResponse Model class
 *
 * @author dja7cob
 */
public class RvwQnaireResponse implements Cloneable, Comparable<RvwQnaireResponse>, IDataObject {

  /**
   *
   */
  private static final long serialVersionUID = -511129323996777488L;
  /**
   * When Object are equal
   */
  private static final int OBJ_EQUAL_CHK_VAL = 0;
  /**
   * Qnaire Resp Id
   */
  private Long id;
  /**
   * Qnaire name
   */

  private String name;
  /**
   * Pidc Vers Id
   */
  private Long pidcVersId;
  /**
   * Pidc Id
   */
  private Long pidcId;
  /**
   * Variant Id
   */
  private Long variantId;

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
   * Variant Name
   */
  private String variantName;

  /**
   * questionnaire is reviewed
   *
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  private boolean isReviewed;

  /**
   * Reviewed User
   *
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  private String reviewedUser;

  /**
   * Reviewed Date
   *
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  private String reviewedDate;

  /**
   * deleted Flag
   */
  private boolean deletedFlag;

  /**
   * deleted Flag
   */
  private Long a2lWpId;

  /**
   * deleted Flag
   */
  private Long a2lRespId;

  /**
   * Variant, Resp, WP name combination of Primary Qnaire Resp
   */
  private String primaryVarRespWpName;

  /**
   * <ol>
   * <li>Map of Linked Qnaires</li>
   * <li>Key - Variant Id, Value - Map with Resp Id as Key and Set of WP Id's as value</li>
   * </ol>
   */
  private Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap = new HashMap<>();


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
  public int compareTo(final RvwQnaireResponse object) {

    if ((null != object.getName()) && (object.getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
        object.getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
      return 1;
    }

    if ((null != getName()) && (getName().startsWith(ApicConstants.GENERAL_QUESTIONS) ||
        getName().startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
      return -1;
    }

    int compareName = ModelUtil.compare(getName(), object.getName());

    // When object name is same compare using id
    if (compareName == OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(getId(), object.getId());
    }

    return compareName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    RvwQnaireResponse other = (RvwQnaireResponse) obj;

    return (null != getId()) && (null != other.getId()) ? ModelUtil.isEqual(getId(), other.getId())
        : ModelUtil.isEqual(getName(), other.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId(), getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RvwQnaireResponse clone() {

    RvwQnaireResponse rvwQnaireResp = null;
    try {
      rvwQnaireResp = (RvwQnaireResponse) super.clone();
    }
    catch (CloneNotSupportedException excep) {
      throw new CloneNotSupportedRuntimeException(excep);
    }
    return rvwQnaireResp;
  }

  /**
   * @return the variantName
   */
  public String getVariantName() {
    return this.variantName;
  }

  /**
   * @param variantName the variantName to set
   */
  public void setVariantName(final String variantName) {
    this.variantName = variantName;
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
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    // Implementation not required
  }


  /**
   * @return the isReviewed
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public boolean isReviewed() {
    return this.isReviewed;
  }


  /**
   * @param isReviewed the isReviewed to set
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public void setReviewed(final boolean isReviewed) {
    this.isReviewed = isReviewed;
  }


  /**
   * @return the reviewedUser
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public String getReviewedUser() {
    return this.reviewedUser;
  }


  /**
   * @param reviewedUser the reviewedUser to set
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public void setReviewedUser(final String reviewedUser) {
    this.reviewedUser = reviewedUser;
  }


  /**
   * @return the reviewedDate
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public String getReviewedDate() {
    return this.reviewedDate;
  }


  /**
   * @param reviewedDate the reviewedDate to set
   * @deprecated use the field from RespVersion model
   */
  @Deprecated
  public void setReviewedDate(final String reviewedDate) {
    this.reviewedDate = reviewedDate;
  }


  /**
   * @return the deletedFlag
   */
  public boolean isDeletedFlag() {
    return this.deletedFlag;
  }


  /**
   * @param deletedFlag the deletedFlag to set
   */
  public void setDeletedFlag(final boolean deletedFlag) {
    this.deletedFlag = deletedFlag;
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
   * @return the pidcId
   */
  public Long getPidcId() {
    return this.pidcId;
  }

  /**
   * @param pidcId the pidcId to set
   */
  public void setPidcId(final Long pidcId) {
    this.pidcId = pidcId;
  }


  /**
   * @return the primaryQnaireRespName
   */
  public String getPrimaryVarRespWpName() {
    return this.primaryVarRespWpName;
  }


  /**
   * @param primaryQnaireRespName the primaryQnaireRespName to set
   */
  public void setPrimaryVarRespWpName(final String primaryQnaireRespName) {
    this.primaryVarRespWpName = primaryQnaireRespName;
  }


  /**
   * @return the secondaryQnaireLinkMap
   */
  public Map<Long, Map<Long, Set<Long>>> getSecondaryQnaireLinkMap() {
    return this.secondaryQnaireLinkMap;
  }


  /**
   * @param secondaryQnaireLinkMap the secondaryQnaireLinkMap to set
   */
  public void setSecondaryQnaireLinkMap(final Map<Long, Map<Long, Set<Long>>> secondaryQnaireLinkMap) {
    this.secondaryQnaireLinkMap = secondaryQnaireLinkMap;
  }

}
