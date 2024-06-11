/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class QnaireRespDetails implements Comparable<QnaireRespDetails> {


  private RvwQnaireResponse rvwQnaireResponse;

  private PidcVersion pidcVersion;

  private PidcVariant pidcVariant;

  private A2lWorkPackage a2lWorkPackage;

  private A2lResponsibility a2lResponsibility;

  private boolean retainFlag;

  private boolean selectedFlag;


  /**
   * @return the rvwQnaireResponse
   */
  public RvwQnaireResponse getRvwQnaireResponse() {
    return this.rvwQnaireResponse;
  }


  /**
   * @param rvwQnaireResponse the rvwQnaireResponse to set
   */
  public void setRvwQnaireResponse(final RvwQnaireResponse rvwQnaireResponse) {
    this.rvwQnaireResponse = rvwQnaireResponse;
  }


  /**
   * @return the pidcVariant
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @return the a2lWorkPackage
   */
  public A2lWorkPackage getA2lWorkPackage() {
    return this.a2lWorkPackage;
  }


  /**
   * @param a2lWorkPackage the a2lWorkPackage to set
   */
  public void setA2lWorkPackage(final A2lWorkPackage a2lWorkPackage) {
    this.a2lWorkPackage = a2lWorkPackage;
  }


  /**
   * @return the a2lResponsibility
   */
  public A2lResponsibility getA2lResponsibility() {
    return this.a2lResponsibility;
  }


  /**
   * @param a2lResponsibility the a2lResponsibility to set
   */
  public void setA2lResponsibility(final A2lResponsibility a2lResponsibility) {
    this.a2lResponsibility = a2lResponsibility;
  }


  /**
   * @return the retainFlag
   */
  public boolean isRetainFlag() {
    return this.retainFlag;
  }


  /**
   * @param retainFlag the retainFlag to set
   */
  public void setRetainFlag(final boolean retainFlag) {
    this.retainFlag = retainFlag;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * @return the selected
   */
  public boolean isSelectedFlag() {
    return this.selectedFlag;
  }


  /**
   * @param selectedFlag the selected to set
   */
  public void setSelectedFlag(final boolean selectedFlag) {
    this.selectedFlag = selectedFlag;
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
    QnaireRespDetails other = (QnaireRespDetails) obj;

    boolean isQnaireRespNameEqual =
        ModelUtil.isEqual(getRvwQnaireResponse().getName(), other.getRvwQnaireResponse().getName());

    boolean isVersIdEqual = ModelUtil.isEqual(getPidcVersion().getId(), other.getPidcVersion().getId());

    boolean isVarIdEqual = ModelUtil.isEqual(getPidcVariant().getId(), other.getPidcVariant().getId());

    boolean isWpIdEqual = ModelUtil.isEqual(getA2lWorkPackage().getId(), other.getA2lWorkPackage().getId());

    return isQnaireRespNameEqual && isVersIdEqual && isVarIdEqual && isWpIdEqual;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getRvwQnaireResponse().getName(), getPidcVersion().getId(),
        getPidcVariant().getId(), getA2lWorkPackage().getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QnaireRespDetails qnaireRespDetails) {
    int compare =
        ModelUtil.compare(getRvwQnaireResponse().getName(), qnaireRespDetails.getRvwQnaireResponse().getName());

    // compare using pidc version name
    if (compare == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compare = ModelUtil.compare(getPidcVersion().getName(), qnaireRespDetails.getPidcVersion().getName());
    }
    // When name is same compare using var name
    if (compare == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compare = ModelUtil.compare(getPidcVariant().getName(), qnaireRespDetails.getPidcVariant().getName());
    }
    // compare using work package name
    if (compare == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      compare = ModelUtil.compare(getA2lWorkPackage().getName(), qnaireRespDetails.getA2lWorkPackage().getName());
    }
    return compare;
  }
}
