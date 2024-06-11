/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefEditorDataHandler;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;


/**
 * Input class for ReviewQuestionaireEditor
 *
 * @author dmo5cob
 */
public class ReviewQuestionaireEditorInput implements IEditorInput {

  /**
   * QuestionnaireVersion instance
   */
  private final QuestionnaireVersion selQuestionareVersion;
  private final NodeAccessPageDataHandler nodeAccessBO;
  private Questionnaire questionnaire;
  private QnaireDefBO qnaireDefBo;
  private QnaireDefEditorDataHandler questionnaireEditorDataHandler;

  /**
   * @param questionnaireVersion questionnaireVersion instance
   */
  public ReviewQuestionaireEditorInput(final QuestionnaireVersion questionnaireVersion) {
    this.selQuestionareVersion = questionnaireVersion;
    this.qnaireDefBo = new QnaireDefBO(questionnaireVersion);
    this.questionnaire = this.qnaireDefBo.getQuestionnaire();
    this.nodeAccessBO = new NodeAccessPageDataHandler(this.questionnaire);
    this.questionnaireEditorDataHandler = new QnaireDefEditorDataHandler(this.qnaireDefBo);

  }


  /**
   * @return the nodeAccessBO
   */
  public NodeAccessPageDataHandler getNodeAccessBO() {
    return this.nodeAccessBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Class adapter) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {

    return "Questionare Editor";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return this.selQuestionareVersion.getName() + " - Questionnaire Definition Editor";
  }


  /**
   * @return the selQuestionareVersion
   */
  public QuestionnaireVersion getSelQuestionareVersion() {
    return this.selQuestionareVersion;
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
    ReviewQuestionaireEditorInput other = (ReviewQuestionaireEditorInput) obj;
    return this.selQuestionareVersion.getId().longValue() == other.selQuestionareVersion.getId().longValue();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.selQuestionareVersion.getId().intValue();
  }


  /**
   * @return the questionnaire
   */
  public Questionnaire getQuestionnaire() {
    return this.questionnaire;
  }


  /**
   * @param questionnaire the questionnaire to set
   */
  public void setQuestionnaire(final Questionnaire questionnaire) {
    this.questionnaire = questionnaire;
  }


  /**
   * @return the qnaireDefBo
   */
  public QnaireDefBO getQnaireDefBo() {
    return this.qnaireDefBo;
  }


  /**
   * @param qnaireDefBo the qnaireDefBo to set
   */
  public void setQnaireDefBo(final QnaireDefBO qnaireDefBo) {
    this.qnaireDefBo = qnaireDefBo;
  }


  /**
   * @return the questionnaireEditorDataHandler
   */
  public QnaireDefEditorDataHandler getQuestionnaireEditorDataHandler() {
    return this.questionnaireEditorDataHandler;
  }


  /**
   * @param questionnaireEditorDataHandler the questionnaireEditorDataHandler to set
   */
  public void setQuestionnaireEditorDataHandler(final QnaireDefEditorDataHandler questionnaireEditorDataHandler) {
    this.questionnaireEditorDataHandler = questionnaireEditorDataHandler;
  }
}
