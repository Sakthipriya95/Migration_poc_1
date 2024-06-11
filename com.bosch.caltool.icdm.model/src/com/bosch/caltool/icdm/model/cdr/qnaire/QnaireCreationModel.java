/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.qnaire;

import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;

/**
 * @author apj4cob
 */
public class QnaireCreationModel {

  private QuestionnaireVersion qnaireVersion;
  private Questionnaire qnaire;
  private WorkPackageDivision wpDiv;
  private NodeAccess nodeAccess;


  /**
   * @return the qnaire
   */
  public Questionnaire getQnaire() {
    return this.qnaire;
  }

  /**
   * @param qnaire the qnaire to set
   */
  public void setQnaire(final Questionnaire qnaire) {
    this.qnaire = qnaire;
  }

  /**
   * @return the qnaireVersion
   */
  public QuestionnaireVersion getQnaireVersion() {
    return this.qnaireVersion;
  }

  /**
   * @param qnaireVersion the qnaireVersion to set
   */
  public void setQnaireVersion(final QuestionnaireVersion qnaireVersion) {
    this.qnaireVersion = qnaireVersion;
  }

  /**
   * @return the wpDiv
   */
  public WorkPackageDivision getWpDiv() {
    return this.wpDiv;
  }

  /**
   * @param wpDiv the wpDiv to set
   */
  public void setWpDiv(final WorkPackageDivision wpDiv) {
    this.wpDiv = wpDiv;
  }

  /**
   * @return the nodeAccess
   */
  public NodeAccess getNodeAccess() {
    return this.nodeAccess;
  }

  /**
   * @param nodeAccess the nodeAccess to set
   */
  public void setNodeAccess(final NodeAccess nodeAccess) {
    this.nodeAccess = nodeAccess;
  }


}
