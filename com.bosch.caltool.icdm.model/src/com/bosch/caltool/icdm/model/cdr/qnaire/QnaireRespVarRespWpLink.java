/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class QnaireRespVarRespWpLink implements Comparable<QnaireRespVarRespWpLink> {

  /**
   * Selected variant object
   */
  private PidcVariant pidcVariant;
  /**
   * Selected A2lResponsibility object
   */
  private A2lResponsibility a2lResponsibility;
  /**
   * Selected A2lWorkPackage object
   */
  private A2lWorkPackage a2lWorkPackage;
  /**
   * Whether variant is linked to selected qnaire resp
   */
  private boolean isLinked;
  /**
   * Selected qnaire resp and variant detail
   */
  private String details;

  /**
   * Selected qnaire response id
   */
  private Long selQnaireRespId;

  /**
   * Qnaire resp variant id to be deleted (Unlinked)
   */
  private Long qnaireRespVarIdToDel;

  /**
   * Whether linking check box is Disabled
   */
  private boolean isDisabled;


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
   * @return the isLinked
   */
  public boolean isLinked() {
    return this.isLinked;
  }


  /**
   * @param isLinked the isLinked to set
   */
  public void setLinked(final boolean isLinked) {
    this.isLinked = isLinked;
  }


  /**
   * @return the details
   */
  public String getDetails() {
    return this.details;
  }


  /**
   * @param details the details to set
   */
  public void setDetails(final String details) {
    this.details = details;
  }


  /**
   * @return the selQnaireRespId
   */
  public Long getSelQnaireRespId() {
    return this.selQnaireRespId;
  }


  /**
   * @param selQnaireRespId the selQnaireRespId to set
   */
  public void setSelQnaireRespId(final Long selQnaireRespId) {
    this.selQnaireRespId = selQnaireRespId;
  }


  /**
   * @return the qnaireRespVarIdToDel
   */
  public Long getQnaireRespVarIdToDel() {
    return this.qnaireRespVarIdToDel;
  }


  /**
   * @param qnaireRespVarIdToDel the qnaireRespVarIdToDel to set
   */
  public void setQnaireRespVarIdToDel(final Long qnaireRespVarIdToDel) {
    this.qnaireRespVarIdToDel = qnaireRespVarIdToDel;
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
   * @return the isDisabled
   */
  public boolean isDisabled() {
    return this.isDisabled;
  }


  /**
   * @param isDisabled the isDisabled to set
   */
  public void setDisabled(final boolean isDisabled) {
    this.isDisabled = isDisabled;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QnaireRespVarRespWpLink obj) {
    int compareVariant = ModelUtil.compare(getPidcVariant().getName(), obj.getPidcVariant().getName());
    // When Not Selecting the Work Package and Responsibility in the Define Qnaire Dialog,
    // A2lResponsibility and A2lWorkPackage will be null
    if ((compareVariant == 0) && (getA2lResponsibility() != null)) {
      int compareResp = ModelUtil.compare(getA2lResponsibility().getName(), obj.getA2lResponsibility().getName());
      if ((compareResp == 0) && (getA2lWorkPackage() != null)) {
        return ModelUtil.compare(getA2lWorkPackage().getName(), obj.getA2lWorkPackage().getName());
      }
      return compareResp;
    }
    return compareVariant;
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

    return ModelUtil.isEqual(getPidcVariant().getId(), ((QnaireRespVarRespWpLink) obj).getPidcVariant().getId()) &&
        compareA2lResp(getA2lResponsibility(), ((QnaireRespVarRespWpLink) obj).getA2lResponsibility()) &&
        compareA2lWp(getA2lWorkPackage(), ((QnaireRespVarRespWpLink) obj).getA2lWorkPackage()) &&
        ModelUtil.isEqual(getSelQnaireRespId(), ((QnaireRespVarRespWpLink) obj).getSelQnaireRespId());
  }


  // When Not Selecting the Work Package and Responsibility in the Define Qnaire Dialog,
  // A2lResponsibility and A2lWorkPackage will be null
  private boolean compareA2lWp(final A2lWorkPackage a2lWorkPackageObj, final A2lWorkPackage linkA2lWorkPackage) {
    return (a2lWorkPackageObj != null) && (linkA2lWorkPackage != null) &&
        ModelUtil.isEqual(a2lWorkPackageObj.getId(), linkA2lWorkPackage.getId());
  }

  private boolean compareA2lResp(final A2lResponsibility a2lResponsibilityObj,
      final A2lResponsibility linkA2lResponsibility) {
    return (a2lResponsibilityObj != null) && (linkA2lResponsibility != null) &&
        ModelUtil.isEqual(a2lResponsibilityObj.getId(), linkA2lResponsibility.getId());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getPidcVariant().getId(), getA2lResponsibility().getId(),
        getA2lWorkPackage().getId(), getSelQnaireRespId());
  }


}
