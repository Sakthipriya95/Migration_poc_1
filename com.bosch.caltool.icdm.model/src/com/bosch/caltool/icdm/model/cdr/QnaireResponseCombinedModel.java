/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireVersionModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestAttrAndValDepModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponseModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class QnaireResponseCombinedModel implements Comparable<QnaireResponseCombinedModel> {

  /**
   * When Object are equal
   */
  private static final int OBJ_EQUAL_CHK_VAL = 0;

  private RvwQnaireResponseModel rvwQnaireResponseModel;

  private QnaireVersionModel qnaireDefModel;

  private QuestAttrAndValDepModel allQnAttrValDepModel;

  private A2lWorkPackage a2lWorkPackage;

  private A2lResponsibility a2lResponsibility;

  /**
   * @return the rvwQnaireResponseModel
   */
  public RvwQnaireResponseModel getRvwQnaireResponseModel() {
    return this.rvwQnaireResponseModel;
  }


  /**
   * @param rvwQnaireResponseModel the rvwQnaireResponseModel to set
   */
  public void setRvwQnaireResponseModel(final RvwQnaireResponseModel rvwQnaireResponseModel) {
    this.rvwQnaireResponseModel = rvwQnaireResponseModel;
  }


  /**
   * @return the qnaireDefModel
   */
  public QnaireVersionModel getQnaireDefModel() {
    return this.qnaireDefModel;
  }


  /**
   * @param qnaireDefModel the qnaireDefModel to set
   */
  public void setQnaireDefModel(final QnaireVersionModel qnaireDefModel) {
    this.qnaireDefModel = qnaireDefModel;
  }


  /**
   * @return the allQnAttrValDepModel
   */
  public QuestAttrAndValDepModel getAllQnAttrValDepModel() {
    return this.allQnAttrValDepModel;
  }


  /**
   * @param allQnAttrValDepModel the allQnAttrValDepModel to set
   */
  public void setAllQnAttrValDepModel(final QuestAttrAndValDepModel allQnAttrValDepModel) {
    this.allQnAttrValDepModel = allQnAttrValDepModel;
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
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final QnaireResponseCombinedModel object) {

    RvwQnaireResponse objRvwQnrResponse = object.getRvwQnaireResponseModel().getRvwQnrResponse();
    String objName = objRvwQnrResponse.getName();
    if ((null != objName) && (objName.startsWith(ApicConstants.GENERAL_QUESTIONS) ||
        objName.startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
      return 1;
    }

    RvwQnaireResponse rvwQnrResponse = getRvwQnaireResponseModel().getRvwQnrResponse();
    String name = rvwQnrResponse.getName();
    if ((((null != name) && (name.startsWith(ApicConstants.GENERAL_QUESTIONS))) ||
        name.startsWith(ApicConstants.OBD_GENERAL_QUESTIONS))) {
      return -1;
    }

    int compareName = ModelUtil.compare(name, objName);

    // When object name is same compare using id
    if (compareName == OBJ_EQUAL_CHK_VAL) {
      return ModelUtil.compare(rvwQnrResponse.getId(), objRvwQnrResponse.getId());
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
    QnaireResponseCombinedModel other = (QnaireResponseCombinedModel) obj;

    RvwQnaireResponse objRvwQnrResponse = other.getRvwQnaireResponseModel().getRvwQnrResponse();
    RvwQnaireResponse rvwQnrResponse = getRvwQnaireResponseModel().getRvwQnrResponse();
    return (null != rvwQnrResponse.getId()) && (null != objRvwQnrResponse.getId())
        ? ModelUtil.isEqual(rvwQnrResponse.getId(), objRvwQnrResponse.getId())
        : ModelUtil.isEqual(rvwQnrResponse.getName(), objRvwQnrResponse.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    RvwQnaireResponse rvwQnrResponse = getRvwQnaireResponseModel().getRvwQnrResponse();
    return ModelUtil.generateHashCode(rvwQnrResponse.getId(), rvwQnrResponse.getName());
  }

}
