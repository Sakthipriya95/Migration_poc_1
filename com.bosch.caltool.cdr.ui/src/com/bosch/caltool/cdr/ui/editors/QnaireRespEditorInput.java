/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import java.util.SortedSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorInputData;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

// ICDM-1983
/**
 * This class is the editor input for QuestionaireResponseEditor.
 *
 * @author mkl2cob
 */
public class QnaireRespEditorInput implements IEditorInput, ILinkSelectionProvider {

  /** Editor Handler for RvwQnaireAnswer. */
  private final QnaireRespEditorDataHandler qnaireRespEditorDataHandler;

  /**
   * Instantiates a new questionaire response editor input.
   *
   * @param qnaireRespId the qnaire resp id
   * @param variantId linked secondary variant
   */
  public QnaireRespEditorInput(final long qnaireRespId, final PidcVariant variant) {
    super();
    this.qnaireRespEditorDataHandler = new QnaireRespEditorDataHandler(qnaireRespId,variant);
  }

  /**
   * @param rvwQnaireRespVersion as input
   */
  public QnaireRespEditorInput(final RvwQnaireRespVersion rvwQnaireRespVersion) {
    super();
    this.qnaireRespEditorDataHandler = new QnaireRespEditorDataHandler(rvwQnaireRespVersion);
  }

  /**
   * Gets the review ans set.
   *
   * @return the reviewAnsSet
   */
  public SortedSet<RvwQnaireAnswer> getReviewAnsSet() {
    return this.qnaireRespEditorDataHandler.getAllQuestionAnswers();
  }

  /**
   * Gets the quest response.
   *
   * @return the questResponse
   */
  public RvwQnaireResponse getQuestResponse() {
    return this.qnaireRespEditorDataHandler.getQuesResponse();
  }


  /**
   * Gets the qnaire resp editor data handler.
   *
   * @return the qnaireRespEditorDataHandler
   */
  public QnaireRespEditorDataHandler getQnaireRespEditorDataHandler() {
    return this.qnaireRespEditorDataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter) {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean exists() {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "Review Question Response";
  }

  // ICDM-1994
  /**
   * Gets the formatted title name.
   *
   * @return the title of review questionnaire response editor
   */
  public String getFormattedTitleName() {
    return "Questionnaire Response - " + getQnaireRespEditorDataHandler().getNameExt();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPersistableElement getPersistable() {
    // Not applicable
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    return getQnaireRespEditorDataHandler().getNameExt() + " - Questionnaire Response Editor";
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
    QnaireRespEditorInput other = (QnaireRespEditorInput) obj;
    if (getEditorInputSelection() == null) {

      return other.getEditorInputSelection() == null;

    }
    return getEditorInputSelection().equals(other.getEditorInputSelection());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return this.qnaireRespEditorDataHandler.getSelRvwQnaireRespVersion().hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QnaireRespEditorInputData getEditorInputSelection() {
    return this.qnaireRespEditorDataHandler.getQnaireRespEditorInputData();
  }
}
